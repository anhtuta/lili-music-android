package com.anhtuta.lilimusic.core.playback.di

import com.anhtuta.lilimusic.core.playback.ListenCountScheduler
import com.anhtuta.lilimusic.core.playback.Media3PlayerController
import com.anhtuta.lilimusic.core.playback.PlayerController
import com.anhtuta.lilimusic.core.playback.WorkListenCountScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaybackBindModule {
    @Binds
    abstract fun bindPlayerController(impl: Media3PlayerController): PlayerController

    @Binds
    abstract fun bindListenCountScheduler(impl: WorkListenCountScheduler): ListenCountScheduler
}
