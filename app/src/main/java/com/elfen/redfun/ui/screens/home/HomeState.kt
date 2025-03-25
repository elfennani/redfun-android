package com.elfen.redfun.ui.screens.home

import com.elfen.redfun.domain.models.Post

data class HomeState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isFetchingNextPage: Boolean = false,
    val error: String? = null,
    val isFetchingNextPageError: Boolean = false,
    val isError: Boolean = false,
    // ---
    val fetchNextPage: () -> Unit = {},
    val refresh: () -> Unit = {},
)
