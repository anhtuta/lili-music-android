package com.anhtuta.lilimusic.core.database

import android.content.Context
import android.database.Cursor
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DatabaseMigrationTest {
    @Test
    fun migrate2To3AddsCatalogIndexAndKeepsSongsAndLyrics() {
        val db = openVersionedDatabase("migration-2-3.db", version = 2) { createV2Schema(it) }
        insertV2SongAndLyric(db)

        MIGRATION_2_3.migrate(db)

        db.query("PRAGMA table_info(songs)").use { cursor ->
            val columns = columnNames(cursor)
            assertThat(columns).contains("catalogIndex")
        }
        db.query("SELECT title, path, catalogIndex FROM songs WHERE id = 1").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getString(0)).isEqualTo("Hello")
            assertThat(cursor.getString(1)).isEqualTo("hello-artist")
            assertThat(cursor.getInt(2)).isEqualTo(0)
        }
        db.query("SELECT body FROM lyrics WHERE fileName = 'hello.lrc'").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getString(0)).isEqualTo("[00:01.00]Hello")
        }
        db.query("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'downloads'").use { cursor ->
            assertThat(cursor.moveToFirst()).isFalse()
        }
        db.close()
    }

    @Test
    fun migrate3To4CreatesDownloadsAndKeepsSongsAndLyrics() {
        val db = openVersionedDatabase("migration-3-4.db", version = 3) { createV3Schema(it) }
        insertV3SongAndLyric(db)

        MIGRATION_3_4.migrate(db)

        db.query("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'downloads'").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
        }
        db.query("PRAGMA table_info(downloads)").use { cursor ->
            val columns = columnNames(cursor)
            assertThat(columns).containsAtLeast(
                "songPath",
                "songId",
                "title",
                "artist",
                "streamUrl",
                "fileName",
                "coverLocalPath",
                "status",
                "progressPercent",
                "bytesDownloaded",
                "updatedAtEpochMs",
            )
        }
        db.query("SELECT title, path, catalogIndex FROM songs WHERE id = 1").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getString(0)).isEqualTo("Hello")
            assertThat(cursor.getString(1)).isEqualTo("hello-artist")
            assertThat(cursor.getInt(2)).isEqualTo(4)
        }
        db.query("SELECT body FROM lyrics WHERE fileName = 'hello.lrc'").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getString(0)).isEqualTo("[00:01.00]Hello")
        }
        db.query("SELECT COUNT(*) FROM downloads").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getInt(0)).isEqualTo(0)
        }
        db.close()
    }

    private fun openVersionedDatabase(
        dbName: String,
        version: Int,
        onCreate: (SupportSQLiteDatabase) -> Unit,
    ): SupportSQLiteDatabase {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(dbName)
        val helper =
            FrameworkSQLiteOpenHelperFactory().create(
                SupportSQLiteOpenHelper.Configuration
                    .builder(context)
                    .name(dbName)
                    .callback(
                        object : SupportSQLiteOpenHelper.Callback(version) {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                onCreate(db)
                            }

                            override fun onUpgrade(
                                db: SupportSQLiteDatabase,
                                oldVersion: Int,
                                newVersion: Int,
                            ) = Unit
                        },
                    ).build(),
            )
        return helper.writableDatabase
    }

    private fun columnNames(cursor: Cursor): Set<String> {
        val columns = mutableSetOf<String>()
        while (cursor.moveToNext()) {
            columns += cursor.getString(cursor.getColumnIndexOrThrow("name"))
        }
        return columns
    }

    private fun createV2Schema(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS songs (
                id INTEGER NOT NULL PRIMARY KEY,
                title TEXT NOT NULL,
                artist TEXT NOT NULL,
                album TEXT,
                path TEXT NOT NULL,
                type TEXT,
                listens INTEGER NOT NULL,
                fileName TEXT NOT NULL,
                imageName TEXT,
                imageUrl TEXT,
                lyricFileName TEXT,
                zingId TEXT,
                songOfTheYear INTEGER,
                createdDate TEXT,
                formatCreatedDate TEXT,
                streamUrl TEXT NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS lyrics (
                fileName TEXT NOT NULL PRIMARY KEY,
                body TEXT NOT NULL
            )
            """.trimIndent(),
        )
    }

    private fun createV3Schema(db: SupportSQLiteDatabase) {
        createV2Schema(db)
        db.execSQL("ALTER TABLE songs ADD COLUMN catalogIndex INTEGER NOT NULL DEFAULT 0")
    }

    private fun insertV2SongAndLyric(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            INSERT INTO songs (
                id, title, artist, album, path, type, listens, fileName, imageName, imageUrl,
                lyricFileName, zingId, songOfTheYear, createdDate, formatCreatedDate, streamUrl
            ) VALUES (
                1, 'Hello', 'Artist', 'Album', 'hello-artist', 'Vietnam', 4, 'hello.mp3', 'hello.jpg',
                'https://example.com/hello.jpg', 'hello.lrc', NULL, NULL, '2021-06-09', '2021/06/09',
                'https://example.com/api/song/file?file=hello.mp3'
            )
            """.trimIndent(),
        )
        db.execSQL("INSERT INTO lyrics (fileName, body) VALUES ('hello.lrc', '[00:01.00]Hello')")
    }

    private fun insertV3SongAndLyric(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            INSERT INTO songs (
                id, title, artist, album, path, type, listens, fileName, imageName, imageUrl,
                lyricFileName, zingId, songOfTheYear, createdDate, formatCreatedDate, streamUrl, catalogIndex
            ) VALUES (
                1, 'Hello', 'Artist', 'Album', 'hello-artist', 'Vietnam', 4, 'hello.mp3', 'hello.jpg',
                'https://example.com/hello.jpg', 'hello.lrc', NULL, NULL, '2021-06-09', '2021/06/09',
                'https://example.com/api/song/file?file=hello.mp3', 4
            )
            """.trimIndent(),
        )
        db.execSQL("INSERT INTO lyrics (fileName, body) VALUES ('hello.lrc', '[00:01.00]Hello')")
    }
}
