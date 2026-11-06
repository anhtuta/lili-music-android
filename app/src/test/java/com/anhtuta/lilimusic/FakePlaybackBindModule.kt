package com.anhtuta.lilimusic

import com.anhtuta.lilimusic.core.data.download.AudioDownloadEngine
import com.anhtuta.lilimusic.core.data.download.AudioDownloadSnapshot
import com.anhtuta.lilimusic.core.playback.FakePlayerController
import com.anhtuta.lilimusic.core.playback.ListenCountScheduler
import com.anhtuta.lilimusic.core.playback.NoOpListenCountScheduler
import com.anhtuta.lilimusic.core.playback.PlayerController
import com.anhtuta.lilimusic.core.playback.di.PlaybackBindModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PlaybackBindModule::class],
)
object FakePlaybackBindModule {
    @Provides
    @Singleton
    fun playerController(): PlayerController = FakePlayerController()

    @Provides
    @Singleton
    fun listenCountScheduler(): ListenCountScheduler = NoOpListenCountScheduler

    @Provides
    @Singleton
    fun audioDownloadEngine(): AudioDownloadEngine =
        object : AudioDownloadEngine {
            override fun observe(): Flow<List<AudioDownloadSnapshot>> = flowOf(emptyList())

            override fun download(
                id: String,
                streamUrl: String,
            ) = Unit

            override fun remove(id: String) = Unit

            override fun removeAll() = Unit
        }
}
