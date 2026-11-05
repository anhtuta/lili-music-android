package com.anhtuta.lilimusic.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class LiliMusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
