package com.anhtuta.lilimusic.core.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LyricDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: LiliMusicDatabase
    private lateinit var lyricDao: LyricDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    LiliMusicDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        lyricDao = database.lyricDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndGetLyricBody() =
        runTest {
            lyricDao.upsert(LyricEntity(fileName = "song.lrc", body = "[00:01.00]Hi"))

            val stored = lyricDao.getByFileName("song.lrc")
            assertThat(stored?.body).isEqualTo("[00:01.00]Hi")
        }

    @Test
    fun replaceUpdatesCachedBody() =
        runTest {
            val local = RoomLyricLocalDataSource(lyricDao)
            local.upsert("song.trc", "old")
            local.upsert("song.trc", "new")

            assertThat(local.get("song.trc")?.body).isEqualTo("new")
        }

    @Test
    fun deleteRemovesCachedBody() =
        runTest {
            val local = RoomLyricLocalDataSource(lyricDao)
            local.upsert("gone.lrc", "[00:01.00]Bye")
            local.delete("gone.lrc")

            assertThat(local.get("gone.lrc")).isNull()
        }
}
