package com.elfen.redfun.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfen.redfun.data.FeedService
import com.elfen.redfun.domain.models.Post
import com.elfen.redfun.ui.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val feedService: FeedService) : ViewModel() {
    private val _state =
        MutableStateFlow(HomeState(isLoading = true, fetchNextPage = ::fetchNextPage))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            when (val result = feedService.getPosts("best")) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, posts = result.data!!)
                }

                is Resource.Error -> {
                    _state.value =
                        _state.value.copy(isLoading = false, error = result.message, isError = true)
                }
            }
        }
    }

    private fun fetchNextPage() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isFetchingNextPage = true)

            when (val result = feedService.getPosts("best",true)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isFetchingNextPage = false,
                        posts = _state.value.posts + result.data!!
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isFetchingNextPage = false,
                        isFetchingNextPageError = true,
                        error = result.message,
                        isError = true
                    )
                }
            }
        }
    }
}