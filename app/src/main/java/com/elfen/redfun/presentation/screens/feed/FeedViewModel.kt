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
import androidx.paging.map
import com.elfen.redfun.data.mappers.asDomainModel
import com.elfen.redfun.domain.model.DisplayMode
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.repository.FeedRepository
import com.elfen.redfun.domain.usecase.GetFeedPagingUseCase
import com.elfen.redfun.domain.usecase.GetSortingUseCase
import com.elfen.redfun.domain.usecase.UpdateSortingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime

private const val TAG = "FeedViewModel"

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val getFeedPaging: GetFeedPagingUseCase,
    private val updateSorting: UpdateSortingUseCase,
    getSorting: GetSortingUseCase
) : ViewModel() {
    val viewModeFlow = dataStore.data.map {
        val viewModelName = it[stringPreferencesKey("display_mode")];
        DisplayMode.valueOf(viewModelName ?: DisplayMode.MASONRY.name)
    }

    @OptIn(ExperimentalTime::class)
    val state = getSorting().map {
        val sorting = it ?: Sorting.Best

        FeedState(
            isLoading = false,
            posts = getFeedPaging(Feed.Home(sorting)).cachedIn(viewModelScope),
            sorting = sorting,
            onSortingChanged = { newSorting ->
                viewModelScope.launch {
                    Log.d(TAG, "onSortingChanged: $newSorting")
                    updateSorting(newSorting)
                }
            },
            displayMode = DisplayMode.MASONRY,
            onDisplayModeChanged = ::updateViewMode
        )
    }
        .combine(viewModeFlow) { state, viewMode -> state.copy(displayMode = viewMode) }
        .stateIn(viewModelScope, SharingStarted.Lazily, FeedState(isLoading = true))

    private fun updateViewMode(displayMode: DisplayMode) {
        Log.d(TAG, "updateViewMode: $displayMode (${displayMode == DisplayMode.MASONRY})")
        viewModelScope.launch {
            dataStore.edit {
                it[stringPreferencesKey("display_mode")] = displayMode.name

                it[booleanPreferencesKey("nav_bar_shown")] = displayMode != DisplayMode.SCROLLER
            }
        }
    }
}