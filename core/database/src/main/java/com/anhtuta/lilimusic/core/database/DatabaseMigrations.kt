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
