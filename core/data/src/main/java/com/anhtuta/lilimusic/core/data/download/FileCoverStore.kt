package com.anhtuta.lilimusic.core.data.download

import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class FileCoverStore(
    private val coversDir: File,
    private val httpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : CoverStore {
    override suspend fun save(
        songPath: String,
        imageUrl: String,
    ): String? =
        withContext(ioDispatcher) {
            runCatching {
                coversDir.mkdirs()
                val request = Request.Builder().url(imageUrl).build()
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    val body = response.body ?: return@use null
                    val file = File(coversDir, fileName(songPath, imageUrl))
                    file.outputStream().use { output -> body.byteStream().copyTo(output) }
                    file.absolutePath
                }
            }.getOrNull()
        }

    override suspend fun delete(songPath: String) {
        withContext(ioDispatcher) {
            val prefix = filePrefix(songPath)
            coversDir.listFiles()?.filter { it.name.startsWith(prefix) }?.forEach { it.delete() }
        }
    }

    override suspend fun deleteAll() {
        withContext(ioDispatcher) {
            coversDir.listFiles()?.forEach { it.delete() }
        }
    }

    companion object {
        fun filePrefix(songPath: String): String = songPath.hashCode().toUInt().toString(16)

        fun fileName(
            songPath: String,
            imageUrl: String,
        ): String {
            val lastSegment = imageUrl.substringAfterLast('/').substringBefore('?')
            val ext =
                lastSegment
                    .substringAfterLast('.', missingDelimiterValue = "img")
                    .filter { it.isLetterOrDigit() }
                    .take(8)
                    .ifBlank { "img" }
            return "${filePrefix(songPath)}.$ext"
        }
    }
}
