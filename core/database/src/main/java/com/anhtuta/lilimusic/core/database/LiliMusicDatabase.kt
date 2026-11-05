package com.anhtuta.lilimusic.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SongEntity::class, LyricEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class LiliMusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    abstract fun lyricDao(): LyricDao
}
