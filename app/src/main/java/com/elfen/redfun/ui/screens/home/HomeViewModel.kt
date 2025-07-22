package com.elfen.redfun.ui.screens.home

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
import com.elfen.redfun.ui.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
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

        HomeState(
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
        .stateIn(viewModelScope, SharingStarted.Lazily, HomeState(isLoading = true))

    private fun updateSorting(sorting: Sorting) {
        viewModelScope.launch {
            feedService.setSorting(sorting)
        }
    }

    private fun updateViewMode(displayMode: DisplayMode) {
        viewModelScope.launch {
            dataStore.updateData {
                it.toMutablePreferences().apply {
                    set(stringPreferencesKey("display_mode"), displayMode.name)
                }
            }
        }
    }
}