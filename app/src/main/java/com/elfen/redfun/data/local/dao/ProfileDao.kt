package com.elfen.redfun.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.elfen.redfun.data.local.models.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Upsert
    suspend fun upsertProfile(profile: ProfileEntity)

    @Query("SELECT * FROM profile WHERE id = :userId")
    fun getProfileByUserID(userId: String): Flow<ProfileEntity?>

    @Query("SELECT * FROM profile WHERE username = :username")
    fun getProfileByUsername(username: String): Flow<ProfileEntity?>

}