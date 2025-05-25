package com.elfen.redfun.ui.screens.home

import android.util.Log
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
import com.elfen.redfun.domain.models.MediaImage
import com.elfen.redfun.domain.models.Post
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.ui.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedService: FeedService,
    private val profileService: ProfileService,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    val viewModeFlow = dataStore.data.map {
        val viewModelName = it[stringPreferencesKey("view_mode")];
        ViewMode.valueOf(viewModelName ?: ViewMode.MASONRY.name)
    }

    @OptIn(ExperimentalTime::class)
    val state = feedService.getSortingFlow().map {
        val sorting = it ?: Sorting.Best

        HomeState(
            isLoading = false,
            posts = feedService.getFeedPaging(sorting).map {
                it.map { feedPost ->
                    val post = feedPost.asAppModel()
                    post
                }
            }.cachedIn(viewModelScope),
            sorting = sorting,
            onSortingChanged = ::updateSorting,
            viewMode = ViewMode.MASONRY,
            onViewModeChanged = ::updateViewMode
        )
    }
        .combine(viewModeFlow) { state, viewMode -> state.copy(viewMode = viewMode) }
        .stateIn(viewModelScope, SharingStarted.Lazily, HomeState(isLoading = true))

    private val _sidebarState = MutableStateFlow(SidebarState(isLoading = true))
    val sidebarState = _sidebarState.asStateFlow()

    init {
        viewModelScope.launch {
            when (val profile = profileService.getActiveProfile()) {
                is Resource.Success -> {
                    _sidebarState.value = _sidebarState.value.copy(
                        isLoading = false,
                        user = profile.data
                    )
                }

                else -> {}
            }
        }
    }

    private fun updateSorting(sorting: Sorting) {
        viewModelScope.launch {
            feedService.setSorting(sorting)
        }
    }

    private fun updateViewMode(viewMode: ViewMode) {
        viewModelScope.launch {
            dataStore.updateData {
                it.toMutablePreferences().apply {
                    set(stringPreferencesKey("view_mode"), viewMode.name)
                }
            }
        }
    }
}