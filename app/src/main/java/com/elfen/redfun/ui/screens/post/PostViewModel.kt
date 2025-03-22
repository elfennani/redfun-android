package com.elfen.redfun.ui.screens.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.redfun.data.remote.APIService
import com.elfen.redfun.data.remote.models.Comment
import com.elfen.redfun.data.remote.models.Link
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val apiService: APIService): ViewModel() {
    private val id = savedStateHandle.toRoute<PostRoute>().id
    val state = MutableStateFlow(id).asStateFlow()

    val post = MutableStateFlow<Link?>(null)
    val comments = MutableStateFlow<List<Comment>>(emptyList())

    init {
        viewModelScope.launch {
            val response = apiService.getComments(id)

            post.value = response.data
            comments.value = response.comments
        }
    }
}