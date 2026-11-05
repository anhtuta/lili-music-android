package com.anhtuta.lilimusic.core.network

import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.google.common.truth.Truth.assertThat
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit

class LumenApiTest {
    private lateinit var server: MockWebServer
    private lateinit var dataSource: LumenNetworkDataSource

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val json =
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(server.url("/"))
                .client(
                    OkHttpClient
                        .Builder()
                        .callTimeout(5, TimeUnit.SECONDS)
                        .build(),
                ).addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        dataSource = RetrofitLumenNetworkDataSource(retrofit.create(LumenApi::class.java))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getSongsParsesUnpagedArrayAndSendsWhitelistedSort() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody(fixture("api/song-all.json")),
            )

            val songs = dataSource.getSongs(SongSort.TITLE, SortOrder.ASC)

            assertThat(songs).hasSize(4)
            assertThat(songs.map { it.id }).containsExactly(1L, 154L, 10L, 44L).inOrder()
            assertThat(songs[0].fileName).contains("123我愛你")
            assertThat(songs[3].fileName).isNull()
            assertThat(songs[3].zingId).isEqualTo("ZWADZBB9")

            val request = server.takeRequest()
            assertThat(request.path).isEqualTo("/api/song/all?sortBy=title&sortOrder=ASC")
        }

    @Test
    fun getSongsSendsListensSortDescending() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody("[]"),
            )

            dataSource.getSongs(SongSort.LISTENS, SortOrder.DESC)

            val request = server.takeRequest()
            assertThat(request.path).isEqualTo("/api/song/all?sortBy=listens&sortOrder=DESC")
        }

    @Test
    fun getSongsSendsCreatedDateWhitelistValue() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody("[]"),
            )

            dataSource.getSongs(SongSort.CREATED_DATE, SortOrder.DESC)

            val request = server.takeRequest()
            assertThat(request.path).isEqualTo("/api/song/all?sortBy=createdDate&sortOrder=DESC")
        }

    @Test
    fun recordListenSendsEncodedPathAndAcceptsEmptyBody() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody(""),
            )

            dataSource.recordListen("123 我愛你")

            val request = server.takeRequest()
            assertThat(request.method).isEqualTo("PUT")
            assertThat(request.path).isEqualTo("/api/song/listens?path=123%20%E6%88%91%E6%84%9B%E4%BD%A0")
        }

    @Test
    fun recordListenHttpErrorSurfacesAsHttpException() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                    .setBody("Song doesn't exist!"),
            )

            val thrown =
                runCatching {
                    dataSource.recordListen("missing")
                }.exceptionOrNull()

            assertThat(thrown).isInstanceOf(HttpException::class.java)
            assertThat((thrown as HttpException).code()).isEqualTo(404)
        }

    @Test
    fun httpErrorSurfacesAsHttpException() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .setBody("SQLSTATE"),
            )

            val thrown =
                runCatching {
                    dataSource.getSongs(SongSort.TITLE, SortOrder.ASC)
                }.exceptionOrNull()

            assertThat(thrown).isInstanceOf(HttpException::class.java)
            assertThat((thrown as HttpException).code()).isEqualTo(500)
        }

    private fun fixture(path: String): String {
        val stream =
            checkNotNull(javaClass.classLoader?.getResourceAsStream(path)) {
                "Missing fixture $path"
            }
        return stream.bufferedReader().use { it.readText() }
    }
}
