package com.anhtuta.lilimusic.core.data.download

import com.google.common.truth.Truth.assertThat
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FileCoverStoreTest {
    private lateinit var server: MockWebServer
    private lateinit var dir: File
    private lateinit var store: FileCoverStore

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        dir = Files.createTempDirectory("covers").toFile()
        store = FileCoverStore(dir, OkHttpClient(), UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        server.shutdown()
        dir.deleteRecursively()
    }

    @Test
    fun saveWritesBodyAndDeleteRemovesPrefixFiles() =
        runTest {
            server.enqueue(MockResponse().setBody("jpeg-bytes").setResponseCode(200))
            val url = server.url("/cover.jpg").toString()

            val path = store.save("hello-artist", url)

            assertThat(path).isNotNull()
            assertThat(File(path!!).readText()).isEqualTo("jpeg-bytes")

            store.delete("hello-artist")

            assertThat(dir.listFiles()).isEmpty()
        }

    @Test
    fun httpErrorReturnsNull() =
        runTest {
            server.enqueue(MockResponse().setResponseCode(404))

            val path = store.save("hello-artist", server.url("/missing.png").toString())

            assertThat(path).isNull()
        }

    @Test
    fun deleteAllClearsDirectory() =
        runTest {
            server.enqueue(MockResponse().setBody("a").setResponseCode(200))
            server.enqueue(MockResponse().setBody("b").setResponseCode(200))
            store.save("one", server.url("/a.jpg").toString())
            store.save("two", server.url("/b.jpg").toString())

            store.deleteAll()

            assertThat(dir.listFiles()).isEmpty()
        }

    @Test
    fun fileNameUsesPathHashAndImageExtension() {
        val name = FileCoverStore.fileName("hello-artist", "https://example.com/pic.PNG?x=1")
        assertThat(name).startsWith(FileCoverStore.filePrefix("hello-artist"))
        assertThat(name).endsWith(".PNG")
    }
}
