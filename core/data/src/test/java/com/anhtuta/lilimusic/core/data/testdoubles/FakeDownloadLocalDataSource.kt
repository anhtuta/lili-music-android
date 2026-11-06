package com.anhtuta.lilimusic.core.data.testdoubles

import com.anhtuta.lilimusic.core.database.DownloadEntity
import com.anhtuta.lilimusic.core.database.DownloadLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeDownloadLocalDataSource : DownloadLocalDataSource {
    private val rows = MutableStateFlow<List<DownloadEntity>>(emptyList())

    override fun observeDownloads(): Flow<List<DownloadEntity>> = rows

    override fun observeByPath(songPath: String): Flow<DownloadEntity?> = rows.map { list -> list.find { it.songPath == songPath } }

    override suspend fun getByPath(songPath: String): DownloadEntity? = rows.value.find { it.songPath == songPath }

    override suspend fun upsert(download: DownloadEntity) {
        rows.value = rows.value.filterNot { it.songPath == download.songPath } + download
    }

    override suspend fun deleteByPath(songPath: String) {
        rows.value = rows.value.filterNot { it.songPath == songPath }
    }

    override suspend fun deleteAll() {
        rows.value = emptyList()
    }
}
