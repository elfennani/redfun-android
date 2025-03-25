package com.elfen.redfun.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.elfen.redfun.data.local.dao.FeedCursorDao
import com.elfen.redfun.data.local.dao.SessionDao
import com.elfen.redfun.data.local.models.FeedCursorEntity
import com.elfen.redfun.data.local.models.SessionEntity

@Database(
    entities = [SessionEntity::class, FeedCursorEntity::class],
    version = 1,
    autoMigrations = [],
    exportSchema = true
)
abstract class Database: RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun feedCursorDao(): FeedCursorDao
}