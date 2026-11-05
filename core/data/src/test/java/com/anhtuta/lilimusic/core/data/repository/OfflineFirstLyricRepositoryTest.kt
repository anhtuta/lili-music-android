package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.data.testdoubles.FakeLumenNetworkDataSource
import com.anhtuta.lilimusic.core.data.testdoubles.FakeLyricLocalDataSource
import com.anhtuta.lilimusic.core.network.LyricNetworkResult
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstLyricRepositoryTest {
    private lateinit var network: FakeLumenNetworkDataSource
    private lateinit var local: FakeLyricLocalDataSource
    private lateinit var repository: OfflineFirstLyricRepository

    @Before
    fun setUp() {
        network = FakeLumenNetworkDataSource()
        local = FakeLyricLocalDataSource()
        repository =
            OfflineFirstLyricRepository(
                network = network,
                local = local,
                ioDispatcher = UnconfinedTestDispatcher(),
            )
    }

    @Test
    fun blankFileNameIsMissingWithoutNetwork() =
        runTest {
            val result = repository.loadLyric(null)

            assertThat(result).isEqualTo(LyricLoadResult.Missing)
            assertThat(network.lyricRequests).isEmpty()
        }

    @Test
    fun missingRemoteClearsCache() =
        runTest {
            local.upsert("gone.lrc", "[00:01.00]stale")
            network.lyrics["gone.lrc"] = LyricNetworkResult.Missing

            val result = repository.loadLyric("gone.lrc")

            assertThat(result).isEqualTo(LyricLoadResult.Missing)
            assertThat(local.get("gone.lrc")).isNull()
        }

    @Test
    fun bodyIsParsedAndCached() =
        runTest {
            network.lyrics["song.lrc"] = LyricNetworkResult.Body("[ti:Hi]\n[00:01.00]Hello")

            val result = repository.loadLyric("song.lrc")

            assertThat(result).isInstanceOf(LyricLoadResult.Ready::class.java)
            val ready = result as LyricLoadResult.Ready
            assertThat(ready.lyric.meta.title).isEqualTo("Hi")
            assertThat(
                ready.lyric.lines
                    .single()
                    .words
                    .single()
                    .text,
            ).isEqualTo("Hello")
            assertThat(local.get("song.lrc")?.body).isEqualTo("[ti:Hi]\n[00:01.00]Hello")
        }

    @Test
    fun networkErrorFallsBackToCache() =
        runTest {
            local.upsert("song.trc", "[00:00.463]<249>Hello")
            network.lyricError = IOException("offline")

            val result = repository.loadLyric("song.trc")

            assertThat(result).isInstanceOf(LyricLoadResult.Ready::class.java)
            val ready = result as LyricLoadResult.Ready
            assertThat(
                ready.lyric.words
                    .single()
                    .text,
            ).isEqualTo("Hello")
        }

    @Test
    fun networkErrorWithoutCacheSurfaces() =
        runTest {
            network.lyricError = IOException("offline")

            val result = repository.loadLyric("song.lrc")

            assertThat(result).isInstanceOf(LyricLoadResult.Error::class.java)
        }
}
