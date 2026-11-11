package com.anhtuta.lilimusic.core.datastore

import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricFontSize
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings

data class SettingsPreferenceSnapshot(
    val lyricAlign: String? = null,
    val theme: String? = null,
    val karaokeMode: Boolean? = null,
    val blurMode: Boolean? = null,
    val giganticLine: Boolean? = null,
    val wordAppear: Boolean? = null,
    val lineAppear: Boolean? = null,
    val translate: Boolean? = null,
    val appTheme: String? = null,
    val lyricFontSize: String? = null,
    val keepScreenOnWhileLyrics: Boolean? = null,
)

object SettingsPreferencesParser {
    fun parse(raw: SettingsPreferenceSnapshot): PlayerSettings {
        val defaults = PlayerSettings()
        return PlayerSettings(
            lyricAlign = LyricAlign.fromStorage(raw.lyricAlign),
            lyricTheme = LyricTheme.fromStorage(raw.theme),
            karaokeMode = raw.karaokeMode ?: defaults.karaokeMode,
            blurMode = raw.blurMode ?: defaults.blurMode,
            giganticLine = raw.giganticLine ?: defaults.giganticLine,
            wordAppear = raw.wordAppear ?: defaults.wordAppear,
            lineAppear = raw.lineAppear ?: defaults.lineAppear,
            translate = raw.translate ?: defaults.translate,
            appTheme = AppTheme.fromStorage(raw.appTheme),
            lyricFontSize = LyricFontSize.fromStorage(raw.lyricFontSize),
            keepScreenOnWhileLyrics = raw.keepScreenOnWhileLyrics ?: defaults.keepScreenOnWhileLyrics,
        )
    }
}
