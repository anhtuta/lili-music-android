package com.anhtuta.lilimusic.core.data.repository

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.data.testdoubles.FakeAudioDownloadEngine
import com.anhtuta.lilimusic.core.data.testdoubles.FakeCoverStore
import com.anhtuta.lilimusic.core.data.testdoubles.FakeDownloadLocalDataSource
import com.anhtuta.lilimusic.core.data.testdoubles.FakeLyricRepository
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.LyricRef
import com.anhtuta.lilimusic.core.model.Song
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultDownloadRepositoryTest {
    private lateinit var local: FakeDownloadLocalDataSource
    private lateinit var engine: FakeAudioDownloadEngine
    private lateinit var lyrics: FakeLyricRepository
    private lateinit var covers: FakeCoverStore
    private lateinit var repository: DefaultDownloadRepository

    @Before
    fun setUp() {
        local = FakeDownloadLocalDataSource()
        engine = FakeAudioDownloadEngine()
        lyrics = FakeLyricRepository()
        covers = FakeCoverStore()
        repository =
            DefaultDownloadRepository(
                local = local,
                engine = engine,
                lyricRepository = lyrics,
                coverStore = covers,
                ioDispatcher = UnconfinedTestDispatcher(),
            )
    }

    @Test
    fun downloadEnqueuesAudioAndCachesLyricAndCover() =
        runTest {
            val song = downloadTestSong()

            repository.download(song)

            assertThat(engine.downloadCalls).containsExactly(song.path to song.streamUrl)
            assertThat(lyrics.loaded).containsExactly("hello.lrc")
            assertThat(covers.saved[song.path]).isEqualTo(song.imageUrl)
            repository.observeDownload(song.path).test {
                val row = awaitItem()
                assertThat(row?.status).isEqualTo(DownloadStatus.QUEUED)
                assertThat(row?.coverLocalUri).contains("saved.jpg")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun missingLyricDoesNotFailDownload() =
        runTest {
            lyrics.result = LyricLoadResult.Missing
            val song = downloadTestSong()

            repository.download(song)
            engine.emitCompleted(song.path)

            repository.observeDownload(song.path).test {
                assertThat(awaitItem()?.status).isEqualTo(DownloadStatus.COMPLETED)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun lyricFetchErrorDoesNotFailDownload() =
        runTest {
            lyrics.error = IOException("offline lyric")
            repository.download(downloadTestSong())
            engine.emitCompleted("hello-artist")

            repository.observeDownload("hello-artist").test {
                assertThat(awaitItem()?.status).isEqualTo(DownloadStatus.COMPLETED)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun engineProgressUpdatesStoredPercent() =
        runTest {
            repository.download(downloadTestSong())
            engine.emitProgress("hello-artist", percent = 40)

            repository.observeDownload("hello-artist").test {
                val row = awaitItem()
                assertThat(row?.status).isEqualTo(DownloadStatus.DOWNLOADING)
                assertThat(row?.progressPercent).isEqualTo(40)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun engineFailureMarksFailedAndRetryReenqueues() =
        runTest {
            val song = downloadTestSong()
            repository.download(song)
            engine.emitFailed(song.path, "disk")

            repository.observeDownload(song.path).test {
                assertThat(awaitItem()?.status).isEqualTo(DownloadStatus.FAILED)
                cancelAndIgnoreRemainingEvents()
            }

            engine.downloadCalls.clear()
            repository.retry(song)

            assertThat(engine.downloadCalls).hasSize(1)
        }

    @Test
    fun completedDownloadIsNotEnqueuedAgain() =
        runTest {
            val song = downloadTestSong()
            repository.download(song)
            engine.emitCompleted(song.path)
            engine.downloadCalls.clear()

            repository.download(song)

            assertThat(engine.downloadCalls).isEmpty()
        }

    @Test
    fun removeDeletesMetadataCoverAndAudio() =
        runTest {
            val song = downloadTestSong()
            repository.download(song)
            lyrics.loaded.clear()

            repository.remove(song.path)

            assertThat(engine.removed).containsExactly(song.path)
            assertThat(covers.deleted).containsExactly(song.path)
            assertThat(local.getByPath(song.path)).isNull()
            assertThat(lyrics.loaded).isEmpty()
        }

    @Test
    fun removeAllClearsEngineCoverAndRows() =
        runTest {
            repository.download(downloadTestSong())
            repository.download(downloadTestSong(id = 2, path = "other"))

            repository.removeAll()

            assertThat(engine.removedAll).isTrue()
            assertThat(covers.deletedAll).isTrue()
            repository.observeDownloads().test {
                assertThat(awaitItem()).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun observeHasActiveDownloadsTracksQueuedAndDownloading() =
        runTest {
            repository.observeHasActiveDownloads().test {
                assertThat(awaitItem()).isFalse()
                repository.download(downloadTestSong())
                awaitItemUntil { it }
                engine.emitCompleted("hello-artist")
                awaitItemUntil { !it }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun observeCompletedPathsTracksFinishedDownloads() =
        runTest {
            repository.download(downloadTestSong())
            repository.observeCompletedPaths().test {
                assertThat(awaitItem()).isEmpty()
                engine.emitCompleted("hello-artist")
                assertThat(awaitItem()).containsExactly("hello-artist")
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun downloadTestSong(
        id: Long = 1L,
        path: String = "hello-artist",
    ): Song =
        Song(
            id = id,
            title = "Hello",
            artist = "Artist",
            album = "Album",
            path = path,
            type = "Vietnam",
            listens = 10,
            fileName = "hello.mp3",
            imageName = "hello.jpg",
            imageUrl = "https://example.com/hello.jpg",
            lyric = LyricRef("hello.lrc"),
            zingId = null,
            songOfTheYear = null,
            createdDate = null,
            formatCreatedDate = null,
            streamUrl = "https://example.com/api/song/file?file=hello.mp3",
        )
}

private suspend fun ReceiveTurbine<Boolean>.awaitItemUntil(predicate: (Boolean) -> Boolean): Boolean {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}
