package com.elfen.redfun.presentation.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.redfun.domain.repository.FeedRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val feedRepositoryImpl: FeedRepository
) : ViewModel() {
    private val id = savedStateHandle.toRoute<PostDetailRoute>().id

    val state = feedRepositoryImpl.getPostWithComments(id).map {
        PostDetailUiState(
            post = it.first,
            comments = it.second,
            isLoading = it.second === null
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PostDetailUiState(null, null, true)
    )
}