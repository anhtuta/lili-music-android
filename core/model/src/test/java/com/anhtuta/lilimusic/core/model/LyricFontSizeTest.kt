package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricFontSizeTest {
    @Test
    fun defaultLevelIsNormal() {
        assertThat(PlayerSettings().lyricFontSize).isEqualTo(LyricFontSize.NORMAL)
        assertThat(LyricFontSize.NORMAL.scale).isEqualTo(1f)
    }

    @Test
    fun fromStorageFallsBackToNormal() {
        assertThat(LyricFontSize.fromStorage(null)).isEqualTo(LyricFontSize.NORMAL)
        assertThat(LyricFontSize.fromStorage("unknown")).isEqualTo(LyricFontSize.NORMAL)
        assertThat(LyricFontSize.fromStorage(LyricFontSize.EXTRA_LARGE.storageValue)).isEqualTo(LyricFontSize.EXTRA_LARGE)
    }
}
