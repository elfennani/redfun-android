package com.elfen.redfun.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.elfen.redfun.data.local.models.SortingEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface SortingDao {
    @Upsert
    suspend fun upsert(entity: SortingEntity)

    @Query("SELECT * FROM sorting WHERE userId = :userId")
    suspend fun getSorting(userId: String): SortingEntity?

    @Query("SELECT * FROM sorting WHERE userId = :userId")
    fun sortingFlow(userId: String): Flow<SortingEntity?>

    @Query("SELECT * FROM sorting")
    fun allSortingFlow(): Flow<List<SortingEntity>>
}