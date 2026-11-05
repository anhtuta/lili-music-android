package com.anhtuta.lilimusic.di

import com.anhtuta.lilimusic.BuildConfig
import com.anhtuta.lilimusic.core.network.di.ApiBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @ApiBaseUrl
    fun provideApiBaseUrl(): String = BuildConfig.API_BASE_URL
}
