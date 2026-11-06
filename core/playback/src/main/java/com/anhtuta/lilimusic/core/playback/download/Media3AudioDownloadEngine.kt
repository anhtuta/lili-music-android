@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback.download

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import com.anhtuta.lilimusic.core.data.download.AudioDownloadEngine
import com.anhtuta.lilimusic.core.data.download.AudioDownloadSnapshot
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Singleton
class Media3AudioDownloadEngine
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val infrastructure: DownloadInfrastructure,
    ) : AudioDownloadEngine {
        private val downloadManager = infrastructure.downloadManager

        init {
            DownloadService.start(context, LiliDownloadService::class.java)
        }

        override fun observe(): Flow<List<AudioDownloadSnapshot>> =
            callbackFlow {
                val listener =
                    object : DownloadManager.Listener {
                        override fun onInitialized(downloadManager: DownloadManager) {
                            trySend(currentSnapshots())
                        }

                        override fun onDownloadChanged(
                            downloadManager: DownloadManager,
                            download: Download,
                            finalException: Exception?,
                        ) {
                            trySend(currentSnapshots())
                        }

                        override fun onDownloadRemoved(
                            downloadManager: DownloadManager,
                            download: Download,
                        ) {
                            trySend(currentSnapshots())
                        }
                    }
                downloadManager.addListener(listener)
                trySend(currentSnapshots())
                awaitClose { downloadManager.removeListener(listener) }
            }.distinctUntilChanged()

        override fun download(
            id: String,
            streamUrl: String,
        ) {
            val request = DownloadRequest.Builder(id, streamUrl.toUri()).build()
            DownloadService.sendAddDownload(
                context,
                LiliDownloadService::class.java,
                request,
                // foreground=
                true,
            )
        }

        override fun remove(id: String) {
            DownloadService.sendRemoveDownload(
                context,
                LiliDownloadService::class.java,
                id,
                // foreground=
                false,
            )
        }

        override fun removeAll() {
            DownloadService.sendRemoveAllDownloads(
                context,
                LiliDownloadService::class.java,
                // foreground=
                false,
            )
        }

        private fun currentSnapshots(): List<AudioDownloadSnapshot> =
            runCatching {
                val snapshots = mutableListOf<AudioDownloadSnapshot>()
                downloadManager.downloadIndex.getDownloads().use { cursor ->
                    while (cursor.moveToNext()) {
                        snapshots += DownloadStateMapper.snapshot(cursor.download)
                    }
                }
                snapshots
            }.getOrDefault(emptyList())
    }
