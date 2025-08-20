package com.elfen.redfun.presentation.screens.feed

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.elfen.redfun.domain.model.DisplayMode
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime

private const val TAG = "FeedViewModel"

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedPaging: GetFeedPagingUseCase,
    private val updateSorting: UpdateFeedSortingUseCase,
    private val updateDisplayMode: UpdateDisplayModeUseCase,
    private val updateNavBarShown: UpdateNavBarShownUseCase,
    getSorting: GetFeedSortingUseCase,
    getDisplayMode: GetDisplayModeUseCase,
    getNavBarShown: GetNavBarShownUseCase
) : ViewModel() {

    private val sortingFlow = getSorting(Feed.Home(Sorting.Best))

    @OptIn(ExperimentalCoroutinesApi::class)
    private val postsFlow = sortingFlow
        .map { sorting -> getFeedPaging(Feed.Home(sorting)).cachedIn(viewModelScope) }

    @OptIn(ExperimentalTime::class)
    val state = combine(
        postsFlow,
        sortingFlow,
        getDisplayMode(),
        getNavBarShown()
    ) { posts, sorting, viewMode, isNavBarShown ->
        FeedUiState(
            isLoading = false,
            posts = posts,
            sorting = sorting,
            displayMode = viewMode,
            isNavBarShown = isNavBarShown
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, FeedUiState(isLoading = true))

    fun onEvent(event: FeedEvent) {
        when (event) {
            is FeedEvent.Refetch -> {
                Log.d(TAG, "onEvent: Refetch")
            }

            is FeedEvent.ChangeSorting -> {
                viewModelScope.launch {
                    Log.d(TAG, "onEvent: ChangeSorting ${event.sorting}")
                    updateSorting(feed = Feed.Home(sorting = event.sorting), event.sorting)
                }
            }

            is FeedEvent.ChangeDisplayMode -> {
                Log.d(TAG, "onEvent: ChangeDisplayMode ${event.displayMode}")
                viewModelScope.launch { updateDisplayMode(event.displayMode) }
            }

            is FeedEvent.ToggleNavBar -> {
                Log.d(TAG, "onEvent: ToggleNavBar")
                viewModelScope.launch {
                    val currentState = state.value.isNavBarShown
                    updateNavBarShown(!currentState)
                }
            }
        }
    }
}