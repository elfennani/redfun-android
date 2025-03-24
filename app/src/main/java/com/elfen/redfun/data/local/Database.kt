package com.elfen.redfun.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.elfen.redfun.data.local.dao.SessionDao
import com.elfen.redfun.data.local.models.SessionEntity

@Database(entities = [SessionEntity::class], version = 1, autoMigrations = [])
abstract class Database: RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}