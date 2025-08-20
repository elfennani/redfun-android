package com.elfen.redfun.presentation.screens.subreddit

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.model.Sorting
import com.elfen.redfun.domain.model.name
import com.elfen.redfun.domain.usecase.GetFeedPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubredditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dataStore: DataStore<Preferences>,
    getFeedPaging: GetFeedPagingUseCase
) : ViewModel() {
    val route = savedStateHandle.toRoute<SubredditRoute>()

    val sorting = dataStore.data.map {
        val sortingName = it[stringPreferencesKey("sorting_${route.name}")]
        sortingName?.let { Sorting.fromName(it) } ?: Sorting.Hot
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts = sorting.flatMapLatest { sorting ->
        getFeedPaging(Feed.Subreddit(route.name, sorting)).cachedIn(viewModelScope)
    }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())


    fun updateSorting(sorting: Sorting) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey("sorting_${route.name}")] = sorting.name()
            }
        }
    }
}