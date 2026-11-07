package com.anhtuta.lilimusic.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anhtuta.lilimusic.core.model.HexColor
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LyricCardPreferences {
    val preferences: Flow<LyricCardSettings>

    suspend fun setTextAlign(align: LyricAlign)

    suspend fun setTextColor(color: String)

    suspend fun setBold(enabled: Boolean)

    suspend fun setBackgroundColor(color: String)

    suspend fun setUseSongImage(enabled: Boolean)

    suspend fun setOverlayOpacity(percent: Int)

    suspend fun setBrightnessBoost(percent: Int)

    suspend fun setFontFamily(font: LyricCardFont)

    suspend fun setFontSize(size: Int)
}

@Singleton
class LyricCardPreferencesDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : LyricCardPreferences {
        override val preferences: Flow<LyricCardSettings> =
            dataStore.data.map { prefs ->
                LyricCardPreferencesParser.parse(
                    LyricCardPreferenceSnapshot(
                        textAlign = prefs[TEXT_ALIGN],
                        textColor = prefs[TEXT_COLOR],
                        isBold = prefs[IS_BOLD],
                        backgroundColor = prefs[BACKGROUND_COLOR],
                        useSongImage = prefs[USE_SONG_IMAGE],
                        overlayOpacity = prefs[OVERLAY_OPACITY],
                        brightnessBoost = prefs[BRIGHTNESS_BOOST],
                        fontFamily = prefs[FONT_FAMILY],
                        fontSize = prefs[FONT_SIZE],
                    ),
                )
            }

        override suspend fun setTextAlign(align: LyricAlign) {
            dataStore.edit { prefs -> prefs[TEXT_ALIGN] = align.storageValue }
        }

        override suspend fun setTextColor(color: String) {
            dataStore.edit { prefs ->
                prefs[TEXT_COLOR] = HexColor.normalize(color, LyricCardSettings.DEFAULT_TEXT_COLOR)
            }
        }

        override suspend fun setBold(enabled: Boolean) {
            dataStore.edit { prefs -> prefs[IS_BOLD] = enabled }
        }

        override suspend fun setBackgroundColor(color: String) {
            dataStore.edit { prefs ->
                prefs[BACKGROUND_COLOR] = HexColor.normalize(color, LyricCardSettings.DEFAULT_BACKGROUND)
            }
        }

        override suspend fun setUseSongImage(enabled: Boolean) {
            dataStore.edit { prefs -> prefs[USE_SONG_IMAGE] = enabled }
        }

        override suspend fun setOverlayOpacity(percent: Int) {
            dataStore.edit { prefs -> prefs[OVERLAY_OPACITY] = LyricCardSettings.clampOverlay(percent) }
        }

        override suspend fun setBrightnessBoost(percent: Int) {
            dataStore.edit { prefs -> prefs[BRIGHTNESS_BOOST] = LyricCardSettings.clampBrightness(percent) }
        }

        override suspend fun setFontFamily(font: LyricCardFont) {
            dataStore.edit { prefs -> prefs[FONT_FAMILY] = font.storageValue }
        }

        override suspend fun setFontSize(size: Int) {
            dataStore.edit { prefs -> prefs[FONT_SIZE] = LyricCardSettings.clampFontSize(size) }
        }

        private companion object {
            val TEXT_ALIGN = stringPreferencesKey("lyricCard_textAlign")
            val TEXT_COLOR = stringPreferencesKey("lyricCard_textColor")
            val IS_BOLD = booleanPreferencesKey("lyricCard_isBold")
            val BACKGROUND_COLOR = stringPreferencesKey("lyricCard_backgroundColor")
            val USE_SONG_IMAGE = booleanPreferencesKey("lyricCard_useSongImage")
            val OVERLAY_OPACITY = intPreferencesKey("lyricCard_overlayOpacity")
            val BRIGHTNESS_BOOST = intPreferencesKey("lyricCard_brightnessBoost")
            val FONT_FAMILY = stringPreferencesKey("lyricCard_fontFamily")
            val FONT_SIZE = intPreferencesKey("lyricCard_fontSize")
        }
    }
