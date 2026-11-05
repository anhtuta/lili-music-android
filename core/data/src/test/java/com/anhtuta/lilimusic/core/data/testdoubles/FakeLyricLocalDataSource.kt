package com.anhtuta.lilimusic.core.data.testdoubles

import com.anhtuta.lilimusic.core.database.LyricEntity
import com.anhtuta.lilimusic.core.database.LyricLocalDataSource

class FakeLyricLocalDataSource : LyricLocalDataSource {
    private val stored = mutableMapOf<String, LyricEntity>()

    override suspend fun get(fileName: String): LyricEntity? = stored[fileName]

    override suspend fun upsert(
        fileName: String,
        body: String,
    ) {
        stored[fileName] = LyricEntity(fileName, body)
    }

    override suspend fun delete(fileName: String) {
        stored.remove(fileName)
    }
}
