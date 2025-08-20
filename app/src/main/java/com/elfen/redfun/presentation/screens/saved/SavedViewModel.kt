package com.elfen.redfun.presentation.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.elfen.redfun.data.FeedService
import com.elfen.redfun.data.local.relations.asAppModel
import com.elfen.redfun.domain.models.Feed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
  private val feedService: FeedService
): ViewModel() {
  val posts =  feedService.getFeedPaging(Feed.SavedPosts).map {
    it.map { feedPost ->
      val post = feedPost.asAppModel()
      post
    }
  }.cachedIn(viewModelScope)

}