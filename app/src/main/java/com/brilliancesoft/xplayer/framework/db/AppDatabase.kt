package com.brilliancesoft.xplayer.framework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.brilliancesoft.xplayer.model.Media

@Database(entities = [Media::class], version = DATA_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}