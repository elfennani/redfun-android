package com.elfen.redfun.ui.screens.subreddit

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
import androidx.paging.map
import com.elfen.redfun.data.FeedService
import com.elfen.redfun.data.local.relations.asAppModel
import com.elfen.redfun.domain.models.Feed
import com.elfen.redfun.domain.models.Post
import com.elfen.redfun.domain.models.Sorting
import com.elfen.redfun.domain.models.name
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
  private val feedService: FeedService,
  private val savedStateHandle: SavedStateHandle,
  private val dataStore: DataStore<Preferences>
): ViewModel() {
  val route = savedStateHandle.toRoute<SubredditRoute>()

  val sorting = dataStore.data.map {
    val sortingName = it[stringPreferencesKey("sorting_${route.name}")]
    sortingName?.let { Sorting.fromName(it) } ?: Sorting.Hot
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  val posts = sorting.flatMapLatest { sorting ->
    feedService.getFeedPaging(Feed.Subreddit(route.name, sorting)).map { feedPost ->
      feedPost.map { it.asAppModel() }
    }.cachedIn(viewModelScope)
  }.cachedIn(viewModelScope).stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())


  fun updateSorting(sorting: Sorting) {
    viewModelScope.launch {
      dataStore.edit { preferences ->
        preferences[stringPreferencesKey("sorting_${route.name}")] = sorting.name()
      }
    }
  }
}