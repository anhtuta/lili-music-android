package com.anhtuta.lilimusic.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS lyrics (fileName TEXT NOT NULL PRIMARY KEY, body TEXT NOT NULL)",
            )
        }
    }

val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS downloads (
                    songPath TEXT NOT NULL PRIMARY KEY,
                    songId INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    artist TEXT NOT NULL,
                    streamUrl TEXT NOT NULL,
                    fileName TEXT NOT NULL,
                    imageUrl TEXT,
                    lyricFileName TEXT,
                    coverLocalPath TEXT,
                    status TEXT NOT NULL,
                    progressPercent INTEGER NOT NULL,
                    bytesDownloaded INTEGER NOT NULL,
                    errorMessage TEXT,
                    updatedAtEpochMs INTEGER NOT NULL
                )
                """.trimIndent(),
            )
        }
    }
