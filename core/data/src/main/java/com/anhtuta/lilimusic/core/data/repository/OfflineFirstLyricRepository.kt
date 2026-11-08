package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.data.di.IoDispatcher
import com.anhtuta.lilimusic.core.database.LyricLocalDataSource
import com.anhtuta.lilimusic.core.lyrics.LyricParser
import com.anhtuta.lilimusic.core.network.LumenNetworkDataSource
import com.anhtuta.lilimusic.core.network.LyricNetworkResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
class OfflineFirstLyricRepository
    @Inject
    constructor(
        private val network: LumenNetworkDataSource,
        private val local: LyricLocalDataSource,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : LyricRepository {
        override suspend fun loadLyric(fileName: String?): LyricLoadResult =
            withContext(ioDispatcher) {
                if (fileName.isNullOrBlank()) {
                    return@withContext LyricLoadResult.Missing
                }
                runCatching {
                    when (val remote = network.getLyric(fileName)) {
                        LyricNetworkResult.Missing -> {
                            local.delete(fileName)
                            LyricLoadResult.Missing
                        }
                        is LyricNetworkResult.Body -> {
                            local.upsert(fileName, remote.text)
                            LyricLoadResult.Ready(LyricParser.parse(remote.text, fileName))
                        }
                    }
                }.getOrElse { error ->
                    val cached = local.get(fileName)
                    if (cached != null) {
                        LyricLoadResult.Ready(LyricParser.parse(cached.body, fileName))
                    } else {
                        LyricLoadResult.Error(error)
                    }
                }
            }

        override suspend fun deleteCached(fileName: String?) {
            withContext(ioDispatcher) {
                if (!fileName.isNullOrBlank()) {
                    local.delete(fileName)
                }
            }
        }
    }
