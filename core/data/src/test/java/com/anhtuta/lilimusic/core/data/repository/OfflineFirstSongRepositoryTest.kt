package com.anhtuta.lilimusic.core.data.repository

import app.cash.turbine.test
import com.anhtuta.lilimusic.core.data.testdoubles.FakeLumenNetworkDataSource
import com.anhtuta.lilimusic.core.data.testdoubles.FakeSongLocalDataSource
import com.anhtuta.lilimusic.core.database.SongEntity
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.network.model.NetworkSong
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstSongRepositoryTest {
    private val baseUrl = "https://blog.nenmongvietnam.com/laravel/public"
    private lateinit var network: FakeLumenNetworkDataSource
    private lateinit var local: FakeSongLocalDataSource
    private lateinit var repository: OfflineFirstSongRepository

    @Before
    fun setUp() {
        network = FakeLumenNetworkDataSource()
        local = FakeSongLocalDataSource()
        repository =
            OfflineFirstSongRepository(
                network = network,
                local = local,
                baseUrl = baseUrl,
                ioDispatcher = UnconfinedTestDispatcher(),
            )
    }

    @Test
    fun observePlayableSongsEmitsCachedSongs() =
        runTest {
            local.seed(listOf(cachedEntity(id = 1, title = "Cached")))

            repository.observePlayableSongs().test {
                val songs = awaitItem()
                assertThat(songs).hasSize(1)
                assertThat(songs.single().title).isEqualTo("Cached")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun refreshWritesOnlyPlayableSongsAndResolvesUrls() =
        runTest {
            network.songs =
                listOf(
                    NetworkSong(
                        id = 1,
                        title = "Playable",
                        artist = "A",
                        path = "playable",
                        fileName = "Charlie Puth - Attention.mp3",
                        imageUrl = "/api/song/picture?file=cover.jpg",
                    ),
                    NetworkSong(
                        id = 44,
                        title = "Zing only",
                        artist = "B",
                        path = "zing",
                        fileName = null,
                        zingId = "ZWADZBB9",
                    ),
                )

            val result = repository.refresh(SongSort.TITLE, SortOrder.ASC)

            assertThat(result.isSuccess).isTrue()
            assertThat(network.requests).containsExactly(SongSort.TITLE to SortOrder.ASC)

            repository.observePlayableSongs().test {
                val songs = awaitItem()
                assertThat(songs).hasSize(1)
                assertThat(songs.single().title).isEqualTo("Playable")
                assertThat(songs.single().imageUrl).isEqualTo("$baseUrl/api/song/picture?file=cover.jpg")
                assertThat(songs.single().streamUrl).isEqualTo(
                    "$baseUrl/api/song/file?file=Charlie%20Puth%20-%20Attention.mp3",
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun refreshPreservesApiOrder() =
        runTest {
            network.songs =
                listOf(
                    NetworkSong(id = 30, title = "C", artist = "A", path = "c", fileName = "c.mp3"),
                    NetworkSong(id = 1, title = "A", artist = "A", path = "a", fileName = "a.mp3"),
                    NetworkSong(id = 20, title = "B", artist = "A", path = "b", fileName = "b.mp3"),
                )

            repository.refresh(SongSort.LISTENS, SortOrder.DESC)

            repository.observePlayableSongs().test {
                assertThat(awaitItem().map { it.id }).containsExactly(30L, 1L, 20L).inOrder()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun failedRefreshKeepsCachedSongs() =
        runTest {
            local.seed(listOf(cachedEntity(id = 1, title = "Cached")))
            network.error = IOException("offline")

            val result = repository.refresh()

            assertThat(result.isFailure).isTrue()
            repository.observePlayableSongs().test {
                assertThat(awaitItem().single().title).isEqualTo("Cached")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun emptyRemoteCatalogClearsCache() =
        runTest {
            local.seed(listOf(cachedEntity(id = 1, title = "Cached")))
            network.songs = emptyList()

            repository.refresh()

            repository.observePlayableSongs().test {
                assertThat(awaitItem()).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun observeSongByPathEmitsCachedMatch() =
        runTest {
            local.seed(listOf(cachedEntity(id = 1, title = "Cached")))

            repository.observeSongByPath("path").test {
                assertThat(awaitItem()?.title).isEqualTo("Cached")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun recordListenForwardsPath() =
        runTest {
            val result = repository.recordListen("hello artist")

            assertThat(result.isSuccess).isTrue()
            assertThat(network.recordedPaths).containsExactly("hello artist")
        }

    @Test
    fun recordListenFailureSurfaces() =
        runTest {
            network.error = IOException("offline")

            val result = repository.recordListen("hello")

            assertThat(result.isFailure).isTrue()
        }

    private fun cachedEntity(
        id: Long,
        title: String,
    ): SongEntity =
        SongEntity(
            id = id,
            title = title,
            artist = "Artist",
            album = null,
            path = "path",
            type = "Vietnam",
            listens = 1,
            fileName = "cached.mp3",
            imageName = null,
            imageUrl = null,
            lyricFileName = null,
            zingId = null,
            songOfTheYear = null,
            createdDate = null,
            formatCreatedDate = null,
            streamUrl = "$baseUrl/api/song/file?file=cached.mp3",
        )
}
