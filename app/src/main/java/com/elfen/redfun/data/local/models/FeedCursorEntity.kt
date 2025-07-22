package com.elfen.redfun.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "feed_cursor", primaryKeys = ["feed", "session_id"])
data class FeedCursorEntity(
    val feed: String,
    val cursor: String,
    @ColumnInfo("session_id") val sessionId: String
)