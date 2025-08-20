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
import com.elfen.redfun.data.FeedService
import com.elfen.redfun.data.ProfileService
import com.elfen.redfun.data.local.relations.asAppModel
import com.elfen.redfun.domain.models.DisplayMode
import com.elfen.redfun.domain.models.Feed
import com.elfen.redfun.domain.models.Sorting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime

private const val TAG = "HomeViewModel"

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedService: FeedService,
    private val profileService: ProfileService,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    val viewModeFlow = dataStore.data.map {
        val viewModelName = it[stringPreferencesKey("display_mode")];
        DisplayMode.valueOf(viewModelName ?: DisplayMode.MASONRY.name)
    }

    @OptIn(ExperimentalTime::class)
    val state = feedService.getSortingFlow().map {
        val sorting = it ?: Sorting.Best

        FeedState(
            isLoading = false,
            posts = feedService.getFeedPaging(Feed.Home(sorting)).map {
                it.map { feedPost ->
                    val post = feedPost.asAppModel()
                    post
                }
            }.cachedIn(viewModelScope),
            sorting = sorting,
            onSortingChanged = ::updateSorting,
            displayMode = DisplayMode.MASONRY,
            onDisplayModeChanged = ::updateViewMode
        )
    }
        .combine(viewModeFlow) { state, viewMode -> state.copy(displayMode = viewMode) }
        .stateIn(viewModelScope, SharingStarted.Lazily, FeedState(isLoading = true))

    private fun updateSorting(sorting: Sorting) {
        viewModelScope.launch {
            feedService.setSorting(sorting)
        }
    }

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