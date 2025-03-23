package com.elfen.redfun.ui.screens.post

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.redfun.data.FeedService
import com.elfen.redfun.data.remote.APIService
import com.elfen.redfun.data.remote.models.RemoteComment
import com.elfen.redfun.data.remote.models.Link
import com.elfen.redfun.domain.models.Comment
import com.elfen.redfun.domain.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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