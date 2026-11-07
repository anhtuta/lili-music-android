package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.datastore.LyricCardPreferences
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DataStoreLyricCardSettingsRepository
    @Inject
    constructor(
        private val preferences: LyricCardPreferences,
    ) : LyricCardSettingsRepository {
        override val settings: Flow<LyricCardSettings> = preferences.preferences

        override suspend fun setTextAlign(align: LyricAlign) {
            preferences.setTextAlign(align)
        }

        override suspend fun setTextColor(color: String) {
            preferences.setTextColor(color)
        }

        override suspend fun setBold(enabled: Boolean) {
            preferences.setBold(enabled)
        }

        override suspend fun setBackgroundColor(color: String) {
            preferences.setBackgroundColor(color)
        }

        override suspend fun setUseSongImage(enabled: Boolean) {
            preferences.setUseSongImage(enabled)
        }

        override suspend fun setOverlayOpacity(percent: Int) {
            preferences.setOverlayOpacity(percent)
        }

        override suspend fun setBrightnessBoost(percent: Int) {
            preferences.setBrightnessBoost(percent)
        }

        override suspend fun setFontFamily(font: LyricCardFont) {
            preferences.setFontFamily(font)
        }

        override suspend fun setFontSize(size: Int) {
            preferences.setFontSize(size)
        }
    }
