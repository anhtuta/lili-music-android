package com.nenmongvietnam.lilimusic.di

import com.nenmongvietnam.lilimusic.BuildConfig
import com.nenmongvietnam.lilimusic.core.network.di.ApiBaseUrl
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
