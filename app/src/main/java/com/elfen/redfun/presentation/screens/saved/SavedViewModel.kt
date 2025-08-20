package com.elfen.redfun.presentation.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.elfen.redfun.data.mappers.asDomainModel
import com.elfen.redfun.domain.model.Feed
import com.elfen.redfun.domain.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
  private val feedRepositoryImpl: FeedRepository
): ViewModel() {
  val posts =  feedRepositoryImpl.getFeedPaging(Feed.SavedPosts).map {
    it.map { feedPost ->
      val post = feedPost.asDomainModel()
      post
    }
  }.cachedIn(viewModelScope)

}