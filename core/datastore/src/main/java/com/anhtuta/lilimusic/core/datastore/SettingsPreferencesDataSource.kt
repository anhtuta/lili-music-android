package com.anhtuta.lilimusic.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsPreferences {
    val preferences: Flow<PlayerSettings>

    suspend fun setLyricAlign(align: LyricAlign)

    suspend fun setLyricTheme(theme: LyricTheme)

    suspend fun setKaraokeMode(enabled: Boolean)

    suspend fun setBlurMode(enabled: Boolean)

    suspend fun setGiganticLine(enabled: Boolean)

    suspend fun setWordAppear(enabled: Boolean)

    suspend fun setLineAppear(enabled: Boolean)

    suspend fun setTranslate(enabled: Boolean)

    suspend fun setAppTheme(theme: AppTheme)
}

@Singleton
class SettingsPreferencesDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : SettingsPreferences {
        override val preferences: Flow<PlayerSettings> =
            dataStore.data.map { prefs ->
                SettingsPreferencesParser.parse(
                    SettingsPreferenceSnapshot(
                        lyricAlign = prefs[LYRIC_ALIGN],
                        theme = prefs[LYRIC_THEME],
                        karaokeMode = prefs[KARAOKE_MODE],
                        blurMode = prefs[BLUR_MODE],
                        giganticLine = prefs[GIGANTIC_LINE],
                        wordAppear = prefs[WORD_APPEAR],
                        lineAppear = prefs[LINE_APPEAR],
                        translate = prefs[TRANSLATE],
                        appTheme = prefs[APP_THEME],
                    ),
                )
            }

        override suspend fun setLyricAlign(align: LyricAlign) {
            dataStore.edit { prefs -> prefs[LYRIC_ALIGN] = align.storageValue }
        }

        override suspend fun setLyricTheme(theme: LyricTheme) {
            dataStore.edit { prefs -> prefs[LYRIC_THEME] = theme.storageValue }
        }

        override suspend fun setKaraokeMode(enabled: Boolean) {
            dataStore.edit { prefs -> prefs[KARAOKE_MODE] = enabled }
        }

        override suspend fun setBlurMode(enabled: Boolean) {
            dataStore.edit { prefs -> prefs[BLUR_MODE] = enabled }
        }

        override suspend fun setGiganticLine(enabled: Boolean) {
            dataStore.edit { prefs -> prefs[GIGANTIC_LINE] = enabled }
        }

        override suspend fun setWordAppear(enabled: Boolean) {
            dataStore.edit { prefs -> prefs[WORD_APPEAR] = enabled }
        }

        override suspend fun setLineAppear(enabled: Boolean) {
            dataStore.edit { prefs -> prefs[LINE_APPEAR] = enabled }
        }

        override suspend fun setTranslate(enabled: Boolean) {
            dataStore.edit { prefs -> prefs[TRANSLATE] = enabled }
        }

        override suspend fun setAppTheme(theme: AppTheme) {
            dataStore.edit { prefs -> prefs[APP_THEME] = theme.storageValue }
        }

        private companion object {
            val LYRIC_ALIGN = stringPreferencesKey("settings_lyric_align")
            val LYRIC_THEME = stringPreferencesKey("settings_lyric_theme")
            val KARAOKE_MODE = booleanPreferencesKey("settings_karaoke_mode")
            val BLUR_MODE = booleanPreferencesKey("settings_blur_mode")
            val GIGANTIC_LINE = booleanPreferencesKey("settings_gigantic_line")
            val WORD_APPEAR = booleanPreferencesKey("settings_word_appear")
            val LINE_APPEAR = booleanPreferencesKey("settings_line_appear")
            val TRANSLATE = booleanPreferencesKey("settings_translate")
            val APP_THEME = stringPreferencesKey("settings_app_theme")
        }
    }
