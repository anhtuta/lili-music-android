package com.anhtuta.lilimusic.core.database

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

interface DownloadLocalDataSource {
    fun observeDownloads(): Flow<List<DownloadEntity>>

    fun observeByPath(songPath: String): Flow<DownloadEntity?>

    suspend fun getByPath(songPath: String): DownloadEntity?

    suspend fun upsert(download: DownloadEntity)

    suspend fun deleteByPath(songPath: String)

    suspend fun deleteAll()
}

@Singleton
class RoomDownloadLocalDataSource
    @Inject
    constructor(
        private val downloadDao: DownloadDao,
    ) : DownloadLocalDataSource {
        override fun observeDownloads(): Flow<List<DownloadEntity>> = downloadDao.observeAll()

        override fun observeByPath(songPath: String): Flow<DownloadEntity?> = downloadDao.observeByPath(songPath)

        override suspend fun getByPath(songPath: String): DownloadEntity? = downloadDao.getByPath(songPath)

        override suspend fun upsert(download: DownloadEntity) {
            downloadDao.upsert(download)
        }

        override suspend fun deleteByPath(songPath: String) {
            downloadDao.deleteByPath(songPath)
        }

        override suspend fun deleteAll() {
            downloadDao.deleteAll()
        }
    }
