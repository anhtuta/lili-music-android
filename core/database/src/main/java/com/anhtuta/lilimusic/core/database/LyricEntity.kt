package com.anhtuta.lilimusic.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lyrics")
data class LyricEntity(
    @PrimaryKey val fileName: String,
    val body: String,
)
