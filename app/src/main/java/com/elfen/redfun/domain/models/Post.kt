package com.elfen.redfun.domain.models

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Post(
    val id: String,
    val body: String?,
    val subreddit: String,
    val score: Int,
    val numComments: Int,
    val author: String,
    val created: Instant,
    val thumbnail: String?,
    val url: String,
    val title: String,
    val nsfw: Boolean,
    val link: String?,
    val images: List<MediaImage>?,
    val video: MediaVideo?
)
