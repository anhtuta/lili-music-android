package com.anhtuta.lilimusic.core.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.DownloadStatus
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
class DownloadDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: LiliMusicDatabase
    private lateinit var downloadDao: DownloadDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    LiliMusicDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        downloadDao = database.downloadDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndObserveDownloads() =
        runTest(UnconfinedTestDispatcher()) {
            downloadDao.observeAll().test {
                assertThat(awaitItem()).isEmpty()

                downloadDao.upsert(downloadEntity(path = "a", title = "Alpha", updatedAt = 2L))
                downloadDao.upsert(downloadEntity(path = "b", title = "Beta", updatedAt = 3L))

                val rows = awaitItem()
                assertThat(rows.map { it.songPath }).containsExactly("b", "a").inOrder()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun observeByPathEmitsMatchingRow() =
        runTest(UnconfinedTestDispatcher()) {
            downloadDao.upsert(downloadEntity(path = "a", title = "Alpha"))

            downloadDao.observeByPath("a").test {
                assertThat(awaitItem()?.title).isEqualTo("Alpha")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun deleteByPathRemovesOneRow() =
        runTest {
            downloadDao.upsert(downloadEntity(path = "a", title = "Alpha"))
            downloadDao.upsert(downloadEntity(path = "b", title = "Beta"))

            downloadDao.deleteByPath("a")

            assertThat(downloadDao.getByPath("a")).isNull()
            assertThat(downloadDao.getByPath("b")?.title).isEqualTo("Beta")
        }

    @Test
    fun deleteAllClearsTable() =
        runTest {
            downloadDao.upsert(downloadEntity(path = "a", title = "Alpha"))
            downloadDao.deleteAll()

            assertThat(downloadDao.getByPath("a")).isNull()
        }

    @Test
    fun entityRoundTripsToDomainUsingFileUri() {
        val entity = downloadEntity(path = "hello", title = "Hello").copy(coverLocalPath = "/tmp/hello.jpg")
        val download = entity.toSongDownload()

        assertThat(download.status).isEqualTo(DownloadStatus.QUEUED)
        assertThat(download.coverLocalUri).startsWith("file:")
        assertThat(download.coverLocalUri).contains("hello.jpg")
        assertThat(download.toEntity().songPath).isEqualTo("hello")
        assertThat(download.toEntity().coverLocalPath).isEqualTo("/tmp/hello.jpg")
    }

    @Test
    fun roomLocalDataSourceDelegates() =
        runTest(UnconfinedTestDispatcher()) {
            val local = RoomDownloadLocalDataSource(downloadDao)
            local.upsert(downloadEntity(path = "a", title = "Alpha"))

            local.observeDownloads().test {
                assertThat(awaitItem().single().title).isEqualTo("Alpha")
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun downloadEntity(
        path: String,
        title: String,
        updatedAt: Long = 1L,
    ): DownloadEntity =
        DownloadEntity(
            songPath = path,
            songId = path.hashCode().toLong(),
            title = title,
            artist = "Artist",
            streamUrl = "https://example.com/api/song/file?file=$path.mp3",
            fileName = "$path.mp3",
            imageUrl = "https://example.com/$path.jpg",
            lyricFileName = "$path.lrc",
            coverLocalPath = null,
            status = DownloadStatus.QUEUED.name,
            progressPercent = 0,
            bytesDownloaded = 0L,
            errorMessage = null,
            updatedAtEpochMs = updatedAt,
        )
}
