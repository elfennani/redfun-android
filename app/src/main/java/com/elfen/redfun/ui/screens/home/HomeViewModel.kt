package com.elfen.redfun.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfen.redfun.data.FeedService
import com.elfen.redfun.data.remote.APIService
import com.elfen.redfun.data.remote.models.Link
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val feedService: FeedService): ViewModel() {
    private val _posts = MutableStateFlow<List<Link>>(emptyList())
    val posts = _posts.asStateFlow()

    init {
        viewModelScope.launch {
            _posts.value = feedService.getPosts("best")
        }
    }
}