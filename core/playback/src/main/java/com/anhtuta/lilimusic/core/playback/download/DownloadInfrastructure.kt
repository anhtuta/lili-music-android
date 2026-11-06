@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback.download

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadInfrastructure
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)
        val cache: Cache =
            SimpleCache(
                File(context.filesDir, PlaybackCachePolicy.CACHE_DIR),
                NoOpCacheEvictor(),
                databaseProvider,
            )
        private val httpFactory = DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT)
        val playbackDataSourceFactory: CacheDataSource.Factory =
            CacheDataSource
                .Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context, httpFactory))
                .setCacheWriteDataSinkFactory(null)
                .setFlags(PlaybackCachePolicy.flags())
        val downloadManager: DownloadManager =
            DownloadManager(
                context,
                databaseProvider,
                cache,
                httpFactory,
                Executors.newFixedThreadPool(DOWNLOAD_THREADS),
            )

        companion object {
            const val USER_AGENT = "LiliMusic"
            const val DOWNLOAD_THREADS = 2
        }
    }
