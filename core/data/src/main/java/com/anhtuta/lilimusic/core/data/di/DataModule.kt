package com.anhtuta.lilimusic.core.data.di

import com.anhtuta.lilimusic.core.data.repository.LyricRepository
import com.anhtuta.lilimusic.core.data.repository.OfflineFirstLyricRepository
import com.anhtuta.lilimusic.core.data.repository.OfflineFirstSongRepository
import com.anhtuta.lilimusic.core.data.repository.SongRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindModule {
    @Binds
    abstract fun bindSongRepository(impl: OfflineFirstSongRepository): SongRepository

    @Binds
    abstract fun bindLyricRepository(impl: OfflineFirstLyricRepository): LyricRepository
}
