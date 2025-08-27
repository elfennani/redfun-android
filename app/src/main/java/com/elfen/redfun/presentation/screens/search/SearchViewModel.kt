package com.elfen.redfun.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.repository.FeedRepository
import com.elfen.redfun.domain.usecase.GetAutoCompleteResultsUseCase
import com.elfen.redfun.domain.usecase.GetDisplayModeUseCase
import com.elfen.redfun.domain.usecase.GetFeedPagingUseCase
import com.elfen.redfun.domain.usecase.GetFeedSortingUseCase
import com.elfen.redfun.domain.usecase.UpdateDisplayModeUseCase
import com.elfen.redfun.domain.usecase.UpdateFeedSortingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getAutoCompleteResults: GetAutoCompleteResultsUseCase,
    private val getFeedPagingUseCase: GetFeedPagingUseCase,
    private val updateFeedSorting: UpdateFeedSortingUseCase,
    private val updateDisplayMode: UpdateDisplayModeUseCase,
    getSorting: GetFeedSortingUseCase,
    getDisplayMode: GetDisplayModeUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    private val sorting = getSorting(Feed.Search("searching", Sorting.Best))
    val displayMode = getDisplayMode("search")
    val state = combine(_state, sorting, displayMode) { state, sorting, displayMode ->
        state.copy(
            sorting = sorting,
            displayMode = displayMode,
            posts = if (state.searchedQuery.isNotEmpty()) getFeedPagingUseCase(
                Feed.Search(
                    query = state.searchedQuery,
                    sorting = sorting,
                    subreddit = state.selectedSubreddit
                )
            ) else state.posts
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, SearchUiState())

    init {
        viewModelScope.launch {
            _state.map { it.query }.distinctUntilChanged().debounce(300L).collect { query ->
                if (query.isNotEmpty()) {
                    _state.update { it.copy(isLoading = true) }
                    val result = getAutoCompleteResults(query)
                    _state.update {
                        it.copy(autoCompleteResult = result)
                    }
                } else {
                    _state.value = state.value.copy()
                }
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> {
                _state.value = state.value.copy(query = event.query)
            }

            is SearchEvent.ClearQuery -> {
                _state.value = state.value.copy()
            }

            is SearchEvent.Search -> {
                searchByQuery(state.value.query)
            }

            is SearchEvent.ChangeSorting -> viewModelScope.launch {
                updateFeedSorting(
                    Feed.Search(
                        query = "searching",
                        sorting = event.sorting
                    ), event.sorting
                )
            }

            is SearchEvent.ChangeDisplayMode -> viewModelScope.launch {
                updateDisplayMode(event.displayMode)
            }

            is SearchEvent.SelectSubreddit -> {
                _state.update {
                    it.copy(
                        selectedSubreddit = event.subreddit,
                        query = if (event.subreddit == null) it.query else "",
                        posts = if (event.subreddit == null) it.posts else getFeedPagingUseCase(
                            Feed.Search(
                                query = it.query,
                                sorting = it.sorting ?: Sorting.Best,
                                subreddit = event.subreddit
                            )
                        )
                    )
                }
            }
        }
    }

    private fun searchByQuery(query: String) {
        if (query.isEmpty()) return
        _state.update { it.copy(searchedQuery = query) }
    }
}