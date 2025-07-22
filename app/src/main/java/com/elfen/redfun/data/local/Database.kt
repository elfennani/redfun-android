package com.elfen.redfun.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.elfen.redfun.data.local.dao.FeedCursorDao
import com.elfen.redfun.data.local.dao.PostDao
import com.elfen.redfun.data.local.dao.ProfileDao
import com.elfen.redfun.data.local.dao.SessionDao
import com.elfen.redfun.data.local.dao.SortingDao
import com.elfen.redfun.data.local.models.FeedCursorEntity
import com.elfen.redfun.data.local.models.FeedPostEntity
import com.elfen.redfun.data.local.models.PostEntity
import com.elfen.redfun.data.local.models.PostMediaEntity
import com.elfen.redfun.data.local.models.ProfileEntity
import com.elfen.redfun.data.local.models.SessionEntity
import com.elfen.redfun.data.local.models.SortingEntity

@Database(
    entities = [
        SessionEntity::class,
        FeedCursorEntity::class,
        SortingEntity::class,
        FeedPostEntity::class,
        PostEntity::class,
        PostMediaEntity::class,
        ProfileEntity::class
    ],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ],
    exportSchema = true
)
abstract class Database : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun feedCursorDao(): FeedCursorDao
    abstract fun sortingDao(): SortingDao
    abstract fun postDao(): PostDao
    abstract fun profileDao(): ProfileDao
}