package com.anhtuta.lilimusic.core.database.di

import android.content.Context
import androidx.room.Room
import com.anhtuta.lilimusic.core.database.DownloadDao
import com.anhtuta.lilimusic.core.database.DownloadLocalDataSource
import com.anhtuta.lilimusic.core.database.LiliMusicDatabase
import com.anhtuta.lilimusic.core.database.LyricDao
import com.anhtuta.lilimusic.core.database.LyricLocalDataSource
import com.anhtuta.lilimusic.core.database.MIGRATION_1_2
import com.anhtuta.lilimusic.core.database.MIGRATION_2_3
import com.anhtuta.lilimusic.core.database.RoomDownloadLocalDataSource
import com.anhtuta.lilimusic.core.database.RoomLyricLocalDataSource
import com.anhtuta.lilimusic.core.database.RoomSongLocalDataSource
import com.anhtuta.lilimusic.core.database.SongDao
import com.anhtuta.lilimusic.core.database.SongLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): LiliMusicDatabase =
        Room
            .databaseBuilder(
                context,
                LiliMusicDatabase::class.java,
                "lili-music.db",
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

    @Provides
    fun provideSongDao(database: LiliMusicDatabase): SongDao = database.songDao()

    @Provides
    fun provideLyricDao(database: LiliMusicDatabase): LyricDao = database.lyricDao()

    @Provides
    fun provideDownloadDao(database: LiliMusicDatabase): DownloadDao = database.downloadDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBindModule {
    @Binds
    abstract fun bindSongLocalDataSource(impl: RoomSongLocalDataSource): SongLocalDataSource

    @Binds
    abstract fun bindLyricLocalDataSource(impl: RoomLyricLocalDataSource): LyricLocalDataSource

    @Binds
    abstract fun bindDownloadLocalDataSource(impl: RoomDownloadLocalDataSource): DownloadLocalDataSource
}
