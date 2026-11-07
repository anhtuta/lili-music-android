package com.anhtuta.lilimusic.feature.lyrics.di

import com.anhtuta.lilimusic.feature.lyrics.AndroidLyricCardClipboard
import com.anhtuta.lilimusic.feature.lyrics.CoilLyricCardCoverLoader
import com.anhtuta.lilimusic.feature.lyrics.FileProviderLyricCardFiles
import com.anhtuta.lilimusic.feature.lyrics.LyricCardClipboard
import com.anhtuta.lilimusic.feature.lyrics.LyricCardCoverLoader
import com.anhtuta.lilimusic.feature.lyrics.LyricCardFiles
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LyricCardModule {
    @Binds
    abstract fun bindCoverLoader(impl: CoilLyricCardCoverLoader): LyricCardCoverLoader

    @Binds
    abstract fun bindFiles(impl: FileProviderLyricCardFiles): LyricCardFiles

    @Binds
    abstract fun bindClipboard(impl: AndroidLyricCardClipboard): LyricCardClipboard
}
