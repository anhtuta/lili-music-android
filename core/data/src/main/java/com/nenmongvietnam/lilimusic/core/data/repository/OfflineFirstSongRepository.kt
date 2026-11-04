package com.nenmongvietnam.lilimusic.core.data.repository

import com.nenmongvietnam.lilimusic.core.data.di.IoDispatcher
import com.nenmongvietnam.lilimusic.core.database.SongLocalDataSource
import com.nenmongvietnam.lilimusic.core.database.toEntity
import com.nenmongvietnam.lilimusic.core.database.toSong
import com.nenmongvietnam.lilimusic.core.model.Song
import com.nenmongvietnam.lilimusic.core.model.SongSort
import com.nenmongvietnam.lilimusic.core.model.SortOrder
import com.nenmongvietnam.lilimusic.core.network.LumenNetworkDataSource
import com.nenmongvietnam.lilimusic.core.network.di.ApiBaseUrl
import com.nenmongvietnam.lilimusic.core.network.model.toPlayableSong
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
