package com.elfen.redfun.presentation.screens.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.redfun.data.FeedService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val feedService: FeedService): ViewModel() {
    private val id = savedStateHandle.toRoute<PostRoute>().id

    val state = feedService.getPostWithComments(id).map {
        PostState(
            post = it.first,
            comments = it.second,
            isLoading = it.second === null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PostState(null, null, true))
}