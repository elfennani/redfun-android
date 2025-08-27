package com.elfen.redfun.presentation.screens.flair

import kotlinx.serialization.Serializable

@Serializable
data class FlairRoute(
    val subreddit: String,
    val flair: String,
)