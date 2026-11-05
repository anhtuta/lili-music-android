package com.anhtuta.lilimusic.core.database.di

import android.content.Context
import androidx.room.Room
import com.anhtuta.lilimusic.core.database.LiliMusicDatabase
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
            ).build()

    @Provides
    fun provideSongDao(database: LiliMusicDatabase): SongDao = database.songDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBindModule {
    @Binds
    abstract fun bindSongLocalDataSource(impl: RoomSongLocalDataSource): SongLocalDataSource
}
