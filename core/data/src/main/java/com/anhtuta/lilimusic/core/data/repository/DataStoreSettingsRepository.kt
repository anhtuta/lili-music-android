package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.datastore.SettingsPreferences
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricFontSize
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DataStoreSettingsRepository
    @Inject
    constructor(
        private val settingsPreferences: SettingsPreferences,
    ) : SettingsRepository {
        override val settings: Flow<PlayerSettings> = settingsPreferences.preferences

        override suspend fun setLyricAlign(align: LyricAlign) {
            settingsPreferences.setLyricAlign(align)
        }

        override suspend fun setLyricTheme(theme: LyricTheme) {
            settingsPreferences.setLyricTheme(theme)
        }

        override suspend fun setKaraokeMode(enabled: Boolean) {
            settingsPreferences.setKaraokeMode(enabled)
        }

        override suspend fun setBlurMode(enabled: Boolean) {
            settingsPreferences.setBlurMode(enabled)
        }

        override suspend fun setGiganticLine(enabled: Boolean) {
            settingsPreferences.setGiganticLine(enabled)
        }

        override suspend fun setWordAppear(enabled: Boolean) {
            settingsPreferences.setWordAppear(enabled)
        }

        override suspend fun setLineAppear(enabled: Boolean) {
            settingsPreferences.setLineAppear(enabled)
        }

        override suspend fun setTranslate(enabled: Boolean) {
            settingsPreferences.setTranslate(enabled)
        }

        override suspend fun setAppTheme(theme: AppTheme) {
            settingsPreferences.setAppTheme(theme)
        }

        override suspend fun setLyricFontSize(size: LyricFontSize) {
            settingsPreferences.setLyricFontSize(size)
        }
    }
