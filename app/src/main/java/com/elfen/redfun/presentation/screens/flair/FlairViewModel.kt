package com.elfen.redfun.presentation.screens.flair

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.usecase.GetDisplayModeUseCase
import com.elfen.redfun.domain.usecase.GetFeedPagingUseCase
import com.elfen.redfun.domain.usecase.GetFeedSortingUseCase
import com.elfen.redfun.domain.usecase.GetNavBarShownUseCase
import com.elfen.redfun.domain.usecase.UpdateDisplayModeUseCase
import com.elfen.redfun.domain.usecase.UpdateFeedSortingUseCase
import com.elfen.redfun.domain.usecase.UpdateNavBarShownUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlairViewModel @Inject() constructor(
    private val getFeedPaging: GetFeedPagingUseCase,
    private val updateSorting: UpdateFeedSortingUseCase,
    private val updateDisplayMode: UpdateDisplayModeUseCase,
    private val updateNavBarShown: UpdateNavBarShownUseCase,
    getFeedSorting: GetFeedSortingUseCase,
    getDisplayMode: GetDisplayModeUseCase,
    getNavBarShown: GetNavBarShownUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<FlairRoute>()
    private val sorting = getFeedSorting(
        Feed.Search(
            "flair:${route.flair}",
            sorting = Sorting.Best,
            subreddit = route.subreddit
        )
    )
    val state =
        combine(sorting, getDisplayMode(), getNavBarShown()) { sorting, displayMode, navBarShown ->
            FlairUiState(
                posts = getFeedPaging(
                    Feed.Search(
                        "flair:${route.flair}",
                        sorting = sorting,
                        subreddit = route.subreddit
                    )
                ).cachedIn(viewModelScope),
                displayMode = displayMode,
                navBarShown = navBarShown,
                sorting = sorting,
                flair = route.flair
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, FlairUiState())

    fun onEvent(event: FlairEvent) {
        when (event) {
            is FlairEvent.ChangeSorting -> {
                viewModelScope.launch {
                    updateSorting(feed = Feed.Home(sorting = event.sorting), event.sorting)
                }
            }

            is FlairEvent.ChangeDisplayMode -> {
                viewModelScope.launch { updateDisplayMode(event.displayMode) }
            }

            is FlairEvent.ToggleNavBar -> {
                viewModelScope.launch {
                    val currentState = state.value.navBarShown
                    updateNavBarShown(!currentState)
                }
            }
        }
    }
}