package com.elfen.redfun.presentation.screens.details

import com.elfen.redfun.domain.model.Comment
import com.elfen.redfun.domain.model.Post

data class PostDetailUiState(
    val post: Post?,
    val comments: List<Comment>?,
    val isLoading: Boolean
)
