package com.anhtuta.lilimusic.core.database

import javax.inject.Inject
import javax.inject.Singleton

interface LyricLocalDataSource {
    suspend fun get(fileName: String): LyricEntity?

    suspend fun upsert(
        fileName: String,
        body: String,
    )

    suspend fun delete(fileName: String)
}

@Singleton
class RoomLyricLocalDataSource
    @Inject
    constructor(
        private val lyricDao: LyricDao,
    ) : LyricLocalDataSource {
        override suspend fun get(fileName: String): LyricEntity? = lyricDao.getByFileName(fileName)

        override suspend fun upsert(
            fileName: String,
            body: String,
        ) {
            lyricDao.upsert(LyricEntity(fileName = fileName, body = body))
        }

        override suspend fun delete(fileName: String) {
            lyricDao.deleteByFileName(fileName)
        }
    }
