package com.anhtuta.lilimusic.core.datastore

import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SettingsPreferencesParserTest {
    @Test
    fun missingValuesUsePlanDefaults() {
        assertThat(SettingsPreferencesParser.parse(SettingsPreferenceSnapshot()))
            .isEqualTo(PlayerSettings())
    }

    @Test
    fun keepScreenOnWhileLyricsDefaultsOff() {
        assertThat(
            SettingsPreferencesParser.parse(
                SettingsPreferenceSnapshot(keepScreenOnWhileLyrics = true),
            ).keepScreenOnWhileLyrics,
        ).isTrue()
        assertThat(SettingsPreferencesParser.parse(SettingsPreferenceSnapshot()).keepScreenOnWhileLyrics)
            .isFalse()
    }

    @Test
    fun unknownThemeFallsBackToPictureBg() {
        val parsed =
            SettingsPreferencesParser.parse(
                SettingsPreferenceSnapshot(
                    lyricAlign = "justify",
                    theme = "neon",
                    karaokeMode = false,
                    appTheme = "system",
                ),
            )
        assertThat(parsed.lyricAlign).isEqualTo(LyricAlign.CENTER)
        assertThat(parsed.lyricTheme).isEqualTo(LyricTheme.PICTURE_BG)
        assertThat(parsed.karaokeMode).isFalse()
        assertThat(parsed.appTheme).isEqualTo(AppTheme.LIGHT)
    }

    @Test
    fun parsesStoredWebValues() {
        val parsed =
            SettingsPreferencesParser.parse(
                SettingsPreferenceSnapshot(
                    lyricAlign = "right",
                    theme = "dark",
                    karaokeMode = false,
                    blurMode = true,
                    giganticLine = true,
                    wordAppear = true,
                    lineAppear = true,
                    translate = true,
                    appTheme = "dark",
                ),
            )
        assertThat(parsed).isEqualTo(
            PlayerSettings(
                lyricAlign = LyricAlign.RIGHT,
                lyricTheme = LyricTheme.DARK,
                karaokeMode = false,
                blurMode = true,
                giganticLine = true,
                wordAppear = true,
                lineAppear = true,
                translate = true,
                appTheme = AppTheme.DARK,
            ),
        )
    }
}
