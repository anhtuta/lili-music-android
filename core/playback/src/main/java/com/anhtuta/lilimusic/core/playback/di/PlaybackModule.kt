@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import com.anhtuta.lilimusic.core.data.download.AudioDownloadEngine
import com.anhtuta.lilimusic.core.playback.ListenCountScheduler
import com.anhtuta.lilimusic.core.playback.Media3PlayerController
import com.anhtuta.lilimusic.core.playback.PlayerController
import com.anhtuta.lilimusic.core.playback.WorkListenCountScheduler
import com.anhtuta.lilimusic.core.playback.download.LiliDownloadService
import com.anhtuta.lilimusic.core.playback.download.Media3AudioDownloadEngine
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlaybackScope

@Module
@InstallIn(SingletonComponent::class)
object PlaybackScopeModule {
    @Provides
    @Singleton
    @PlaybackScope
    fun providePlaybackScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Provides
    @Singleton
    fun provideDownloadNotificationHelper(
        @ApplicationContext context: Context,
    ): DownloadNotificationHelper = DownloadNotificationHelper(context, LiliDownloadService.CHANNEL_ID)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaybackBindModule {
    @Binds
    abstract fun bindPlayerController(impl: Media3PlayerController): PlayerController

    @Binds
    abstract fun bindListenCountScheduler(impl: WorkListenCountScheduler): ListenCountScheduler

    @Binds
    abstract fun bindAudioDownloadEngine(impl: Media3AudioDownloadEngine): AudioDownloadEngine
}
