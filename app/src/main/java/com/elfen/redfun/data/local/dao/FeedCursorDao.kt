package com.elfen.redfun.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.elfen.redfun.data.local.models.FeedCursorEntity

@Dao
interface FeedCursorDao {
    @Query("SELECT cursor FROM feed_cursor WHERE feed = :feed AND session_id = :sessionId")
    suspend fun getCursor(feed: String, sessionId: String): String?

    @Upsert
    suspend fun upsertCursor(cursor: FeedCursorEntity)
}