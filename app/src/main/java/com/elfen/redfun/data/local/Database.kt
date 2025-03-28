package com.elfen.redfun.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.elfen.redfun.data.local.dao.FeedCursorDao
import com.elfen.redfun.data.local.dao.SessionDao
import com.elfen.redfun.data.local.dao.SortingDao
import com.elfen.redfun.data.local.models.FeedCursorEntity
import com.elfen.redfun.data.local.models.SessionEntity
import com.elfen.redfun.data.local.models.SortingEntity

@Database(
    entities = [SessionEntity::class, FeedCursorEntity::class, SortingEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(1, 2)],
    exportSchema = true
)
abstract class Database : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun feedCursorDao(): FeedCursorDao
    abstract fun sortingDao(): SortingDao
}