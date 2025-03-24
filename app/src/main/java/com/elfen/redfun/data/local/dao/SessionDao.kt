package com.elfen.redfun.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.elfen.redfun.data.local.models.SessionEntity

@Dao
interface SessionDao {
    @Query("SELECT * FROM session WHERE userId = :userId")
    suspend fun getSession(userId: String): SessionEntity?

    @Query("DELETE FROM session WHERE userId = :userId")
    suspend fun deleteSession(userId: String)

    @Upsert
    suspend fun upsertSession(session: SessionEntity)
}