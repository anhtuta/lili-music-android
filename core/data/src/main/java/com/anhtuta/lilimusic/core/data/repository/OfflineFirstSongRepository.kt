package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.data.di.IoDispatcher
import com.anhtuta.lilimusic.core.database.SongLocalDataSource
import com.anhtuta.lilimusic.core.database.toEntity
import com.anhtuta.lilimusic.core.database.toSong
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.network.LumenNetworkDataSource
import com.anhtuta.lilimusic.core.network.di.ApiBaseUrl
import com.anhtuta.lilimusic.core.network.model.toPlayableSong
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class OfflineFirstSongRepository
    @Inject
    constructor(
        private val network: LumenNetworkDataSource,
        private val local: SongLocalDataSource,
        @ApiBaseUrl private val baseUrl: String,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : SongRepository {
        override fun observePlayableSongs(): Flow<List<Song>> = local.observeSongs().map { entities -> entities.map { it.toSong() } }

        override suspend fun refresh(
            sort: SongSort,
            order: SortOrder,
        ): Result<Unit> =
            withContext(ioDispatcher) {
                runCatching {
                    val playable = network.getSongs(sort, order).mapNotNull { it.toPlayableSong(baseUrl) }
                    local.replaceAll(playable.map { it.toEntity() })
                }
            }
    }
