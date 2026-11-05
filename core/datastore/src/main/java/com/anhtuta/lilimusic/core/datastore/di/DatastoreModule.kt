package com.anhtuta.lilimusic.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.anhtuta.lilimusic.core.datastore.PlaybackPreferences
import com.anhtuta.lilimusic.core.datastore.PlaybackPreferencesDataSource
import com.anhtuta.lilimusic.core.datastore.SortPreferences
import com.anhtuta.lilimusic.core.datastore.SortPreferencesDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {
    private const val LIBRARY_PREFERENCES = "library_preferences"

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(LIBRARY_PREFERENCES) },
        )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatastoreBindModule {
    @Binds
    abstract fun bindSortPreferences(impl: SortPreferencesDataSource): SortPreferences

    @Binds
    abstract fun bindPlaybackPreferences(impl: PlaybackPreferencesDataSource): PlaybackPreferences
}
