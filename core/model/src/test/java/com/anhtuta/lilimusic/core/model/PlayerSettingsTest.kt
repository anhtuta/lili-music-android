package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlayerSettingsTest {
    @Test
    fun defaultsMatchPlanWhenKeysAreMissing() {
        val settings = PlayerSettings()
        assertThat(settings.lyricAlign).isEqualTo(LyricAlign.CENTER)
        assertThat(settings.lyricTheme).isEqualTo(LyricTheme.PICTURE_BG)
        assertThat(settings.karaokeMode).isTrue()
        assertThat(settings.blurMode).isFalse()
        assertThat(settings.giganticLine).isTrue()
        assertThat(settings.wordAppear).isFalse()
        assertThat(settings.lineAppear).isFalse()
        assertThat(settings.translate).isFalse()
        assertThat(settings.appTheme).isEqualTo(AppTheme.LIGHT)
    }

    @Test
    fun lyricAlignParsesWebValuesAndFallsBackToCenter() {
        assertThat(LyricAlign.fromStorage("left")).isEqualTo(LyricAlign.LEFT)
        assertThat(LyricAlign.fromStorage("center")).isEqualTo(LyricAlign.CENTER)
        assertThat(LyricAlign.fromStorage("right")).isEqualTo(LyricAlign.RIGHT)
        assertThat(LyricAlign.fromStorage(null)).isEqualTo(LyricAlign.CENTER)
        assertThat(LyricAlign.fromStorage("justify")).isEqualTo(LyricAlign.CENTER)
    }

    @Test
    fun lyricThemeParsesWebValuesAndFallsBackToPictureBg() {
        assertThat(LyricTheme.fromStorage("dark")).isEqualTo(LyricTheme.DARK)
        assertThat(LyricTheme.fromStorage("light")).isEqualTo(LyricTheme.LIGHT)
        assertThat(LyricTheme.fromStorage("picture-bg")).isEqualTo(LyricTheme.PICTURE_BG)
        assertThat(LyricTheme.fromStorage(null)).isEqualTo(LyricTheme.PICTURE_BG)
        assertThat(LyricTheme.fromStorage("neon")).isEqualTo(LyricTheme.PICTURE_BG)
    }

    @Test
    fun appThemeParsesWebValuesAndFallsBackToLight() {
        assertThat(AppTheme.fromStorage("light")).isEqualTo(AppTheme.LIGHT)
        assertThat(AppTheme.fromStorage("dark")).isEqualTo(AppTheme.DARK)
        assertThat(AppTheme.fromStorage(null)).isEqualTo(AppTheme.LIGHT)
        assertThat(AppTheme.fromStorage("system")).isEqualTo(AppTheme.LIGHT)
    }

    @Test
    fun storageValuesRoundTrip() {
        LyricAlign.entries.forEach { align ->
            assertThat(LyricAlign.fromStorage(align.storageValue)).isEqualTo(align)
        }
        LyricTheme.entries.forEach { theme ->
            assertThat(LyricTheme.fromStorage(theme.storageValue)).isEqualTo(theme)
        }
        AppTheme.entries.forEach { theme ->
            assertThat(AppTheme.fromStorage(theme.storageValue)).isEqualTo(theme)
        }
    }
}
