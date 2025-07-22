@file:OptIn(ExperimentalTime::class)

package com.elfen.redfun.domain.models

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

sealed class Comment {
    data class Body(
        val id: String,
        val body: String,
        val score: Int,
        val depth: Int,
        val author: String,
        val created: Instant,
        val images: List<MediaImage>?,
    ) : Comment()

    data class More(
        val id: String,
        val depth: Int,
        val count: Int
    ) : Comment()
}