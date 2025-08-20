package com.elfen.redfun.presentation.screens.subreddit

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.usecase.GetDisplayModeUseCase
import com.elfen.redfun.domain.usecase.GetFeedPagingUseCase
import com.elfen.redfun.domain.usecase.GetFeedSortingUseCase
import com.elfen.redfun.domain.usecase.GetNavBarShownUseCase
import com.elfen.redfun.domain.usecase.UpdateFeedSortingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubredditViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val updateFeedSorting: UpdateFeedSortingUseCase,
    savedStateHandle: SavedStateHandle,
    getFeedPaging: GetFeedPagingUseCase,
    getDisplayMode: GetDisplayModeUseCase,
    getSorting: GetFeedSortingUseCase,
    getNavBarShown: GetNavBarShownUseCase
) : ViewModel() {
    val route = savedStateHandle.toRoute<SubredditRoute>()
    val sorting = getSorting(Feed.Subreddit(route.name, Sorting.Best))

    val state = combine(
        sorting,
        getDisplayMode(route.name),
        getNavBarShown()
    ) { sorting, displayMode, isNavBarShown ->
        SubredditUiState(
            posts = getFeedPaging(Feed.Subreddit(route.name, sorting)).cachedIn(viewModelScope),
            isLoading = false,
            sorting = sorting,
            displayMode = displayMode,
            isNavBarShown = isNavBarShown
        )
    }
        .stateIn(viewModelScope, SharingStarted.Lazily, SubredditUiState(isLoading = true))

    private fun updateSorting(sorting: Sorting) {
        viewModelScope.launch {
            updateFeedSorting(Feed.Subreddit(route.name, sorting), sorting)
        }
    }

    fun onEvent(event: SubredditEvent) {
        when (event) {
            is SubredditEvent.UpdateSorting -> updateSorting(event.sorting)
        }
    }
}