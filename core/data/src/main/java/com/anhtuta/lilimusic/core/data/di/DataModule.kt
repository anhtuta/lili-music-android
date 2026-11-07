package com.anhtuta.lilimusic.core.data.di

import android.content.Context
import com.anhtuta.lilimusic.core.data.download.CoverStore
import com.anhtuta.lilimusic.core.data.download.FileCoverStore
import com.anhtuta.lilimusic.core.data.network.ConnectivityNetworkMonitor
import com.anhtuta.lilimusic.core.data.network.NetworkMonitor
import com.anhtuta.lilimusic.core.data.repository.DataStoreLyricCardSettingsRepository
import com.anhtuta.lilimusic.core.data.repository.DataStoreSettingsRepository
import com.anhtuta.lilimusic.core.data.repository.DefaultDownloadRepository
import com.anhtuta.lilimusic.core.data.repository.DownloadRepository
import com.anhtuta.lilimusic.core.data.repository.LyricCardSettingsRepository
import com.anhtuta.lilimusic.core.data.repository.LyricRepository
import com.anhtuta.lilimusic.core.data.repository.OfflineFirstLyricRepository
import com.anhtuta.lilimusic.core.data.repository.OfflineFirstSongRepository
import com.anhtuta.lilimusic.core.data.repository.SettingsRepository
import com.anhtuta.lilimusic.core.data.repository.SongRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideCoverStore(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CoverStore = FileCoverStore(File(context.filesDir, "covers"), okHttpClient, ioDispatcher)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindModule {
    @Binds
    abstract fun bindSongRepository(impl: OfflineFirstSongRepository): SongRepository

    @Binds
    abstract fun bindLyricRepository(impl: OfflineFirstLyricRepository): LyricRepository

    @Binds
    abstract fun bindSettingsRepository(impl: DataStoreSettingsRepository): SettingsRepository

    @Binds
    abstract fun bindLyricCardSettingsRepository(impl: DataStoreLyricCardSettingsRepository): LyricCardSettingsRepository

    @Binds
    abstract fun bindDownloadRepository(impl: DefaultDownloadRepository): DownloadRepository

    @Binds
    abstract fun bindNetworkMonitor(impl: ConnectivityNetworkMonitor): NetworkMonitor
}
