package com.anhtuta.lilimusic.core.data.testdoubles

import com.anhtuta.lilimusic.core.data.download.CoverStore

class FakeCoverStore : CoverStore {
    val saved = mutableMapOf<String, String>()
    val deleted = mutableListOf<String>()
    var deletedAll = false
    var saveResult: String? = "/covers/saved.jpg"
    var saveError: Throwable? = null

    override suspend fun save(
        songPath: String,
        imageUrl: String,
    ): String? {
        saveError?.let { throw it }
        val path = saveResult
        if (path != null) {
            saved[songPath] = imageUrl
        }
        return path
    }

    override suspend fun delete(songPath: String) {
        deleted += songPath
        saved.remove(songPath)
    }

    override suspend fun deleteAll() {
        deletedAll = true
        saved.clear()
    }
}
