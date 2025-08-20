package com.elfen.redfun.presentation.screens.post

import com.elfen.redfun.domain.models.Comment
import com.elfen.redfun.domain.models.Post

data class PostState(
    val post: Post?,
    val comments: List<Comment>?,
    val isLoading: Boolean
)
