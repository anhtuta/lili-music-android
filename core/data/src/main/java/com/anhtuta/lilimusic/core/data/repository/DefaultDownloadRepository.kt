package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.data.di.IoDispatcher
import com.anhtuta.lilimusic.core.data.download.AudioDownloadEngine
import com.anhtuta.lilimusic.core.data.download.AudioDownloadSnapshot
import com.anhtuta.lilimusic.core.data.download.CoverStore
import com.anhtuta.lilimusic.core.database.DownloadLocalDataSource
import com.anhtuta.lilimusic.core.database.toEntity
import com.anhtuta.lilimusic.core.database.toSongDownload
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongDownload
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Singleton
class DefaultDownloadRepository
    @Inject
    constructor(
        private val local: DownloadLocalDataSource,
        private val engine: AudioDownloadEngine,
        private val lyricRepository: LyricRepository,
        private val coverStore: CoverStore,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : DownloadRepository {
        private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

        init {
            scope.launch {
                engine.observe().collect { snapshots ->
                    snapshots.forEach { applySnapshot(it) }
                }
            }
        }

        override fun observeDownloads(): Flow<List<SongDownload>> =
            local.observeDownloads().map { rows -> rows.map { it.toSongDownload() } }

        override fun observeDownload(songPath: String): Flow<SongDownload?> =
            local.observeByPath(songPath).map { entity -> entity?.toSongDownload() }

        override fun observeCompletedPaths(): Flow<Set<String>> =
            observeDownloads().map { downloads ->
                downloads.filter { it.isComplete }.map { it.songPath }.toSet()
            }

        override fun observeHasActiveDownloads(): Flow<Boolean> = observeDownloads().map { downloads -> downloads.any { it.isActive } }

        override suspend fun download(song: Song) {
            withContext(ioDispatcher) {
                val existing = local.getByPath(song.path)
                if (existing?.status == DownloadStatus.COMPLETED.name) {
                    return@withContext
                }
                val queued =
                    SongDownload
                        .queued(song, System.currentTimeMillis())
                        .toEntity()
                        .copy(coverLocalPath = existing?.coverLocalPath)
                local.upsert(queued)
                engine.download(song.path, song.streamUrl)
                cacheSidecars(song)
            }
        }

        override suspend fun retry(song: Song) {
            download(song)
        }

        override suspend fun remove(songPath: String) {
            withContext(ioDispatcher) {
                engine.remove(songPath)
                coverStore.delete(songPath)
                local.deleteByPath(songPath)
            }
        }

        override suspend fun cancel(songPath: String) {
            withContext(ioDispatcher) {
                val existing = local.getByPath(songPath) ?: return@withContext
                engine.remove(songPath)
                coverStore.delete(songPath)
                lyricRepository.deleteCached(existing.lyricFileName)
                local.deleteByPath(songPath)
            }
        }

        override suspend fun removeAll() {
            withContext(ioDispatcher) {
                engine.removeAll()
                coverStore.deleteAll()
                local.deleteAll()
            }
        }

        private suspend fun applySnapshot(snapshot: AudioDownloadSnapshot) {
            if (snapshot.status == DownloadStatus.NONE) return
            val existing = local.getByPath(snapshot.id) ?: return
            local.upsert(
                existing.copy(
                    status = snapshot.status.name,
                    progressPercent = snapshot.progressPercent.coerceIn(0, 100),
                    bytesDownloaded = snapshot.bytesDownloaded.coerceAtLeast(0L),
                    errorMessage = snapshot.errorMessage,
                    updatedAtEpochMs = System.currentTimeMillis(),
                ),
            )
        }

        private suspend fun cacheSidecars(song: Song) {
            runCatching {
                song.lyric?.fileName?.let { lyricRepository.loadLyric(it) }
            }
            val imageUrl = song.imageUrl ?: return
            val saved = runCatching { coverStore.save(song.path, imageUrl) }.getOrNull() ?: return
            val existing = local.getByPath(song.path) ?: return
            local.upsert(
                existing.copy(
                    coverLocalPath = saved,
                    updatedAtEpochMs = System.currentTimeMillis(),
                ),
            )
        }
    }
