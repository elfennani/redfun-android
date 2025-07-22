package com.elfen.redfun.data.local.models

import androidx.room.Entity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(tableName = "feed_post", primaryKeys = ["feed","postId"])
data class FeedPostEntity @OptIn(ExperimentalTime::class) constructor(
    val feed: String,
    val postId: String,
    val created: Long = Clock.System.now().toEpochMilliseconds(),
    val cursor: String? = null,
    val index: Int? = null
)
