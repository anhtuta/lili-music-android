package com.anhtuta.lilimusic.core.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SongDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: LiliMusicDatabase
    private lateinit var songDao: SongDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    LiliMusicDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        songDao = database.songDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndObserveSongs() =
        runTest(UnconfinedTestDispatcher()) {
            songDao.observeAll().test {
                assertThat(awaitItem()).isEmpty()

                songDao.insertAll(listOf(entity(id = 1, title = "A"), entity(id = 2, title = "B")))

                val inserted = awaitItem()
                assertThat(inserted.map { it.id }).containsExactly(1L, 2L)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun observeByPathEmitsMatchingSong() =
        runTest(UnconfinedTestDispatcher()) {
            songDao.insertAll(listOf(entity(id = 1, title = "A"), entity(id = 2, title = "B")))

            songDao.observeByPath("path-2").test {
                val song = awaitItem()
                assertThat(song?.id).isEqualTo(2L)
                assertThat(song?.title).isEqualTo("B")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun replaceAllClearsPreviousRows() =
        runTest(UnconfinedTestDispatcher()) {
            val local = RoomSongLocalDataSource(database, songDao)
            local.replaceAll(listOf(entity(id = 1, title = "Old")))
            local.replaceAll(listOf(entity(id = 2, title = "New")))

            local.observeSongs().test {
                val songs = awaitItem()
                assertThat(songs).hasSize(1)
                assertThat(songs.single().title).isEqualTo("New")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun replaceAllWithEmptyListClearsCache() =
        runTest(UnconfinedTestDispatcher()) {
            val local = RoomSongLocalDataSource(database, songDao)
            local.replaceAll(listOf(entity(id = 1, title = "Gone")))
            local.replaceAll(emptyList())

            local.observeSongs().test {
                assertThat(awaitItem()).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun entityRoundTripsToDomain() {
        val entity = entity(id = 9, title = "Round")
        val song = entity.toSong()
        assertThat(song.fileName).isEqualTo("round.mp3")
        assertThat(song.lyric?.fileName).isEqualTo("round.lrc")
        assertThat(song.toEntity()).isEqualTo(entity)
    }

    private fun entity(
        id: Long,
        title: String,
    ): SongEntity =
        SongEntity(
            id = id,
            title = title,
            artist = "Artist",
            album = "Album",
            path = "path-$id",
            type = "Vietnam",
            listens = 4,
            fileName = "round.mp3",
            imageName = "cover.jpg",
            imageUrl = "https://example.com/cover.jpg",
            lyricFileName = "round.lrc",
            zingId = null,
            songOfTheYear = null,
            createdDate = "2021-06-09T12:21:29.000000Z",
            formatCreatedDate = "2021/06/09",
            streamUrl = "https://example.com/api/song/file?file=round.mp3",
        )
}
