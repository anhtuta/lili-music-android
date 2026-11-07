package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricCardSettingsTest {
    @Test
    fun defaultsMatchWebLyricDownloadModal() {
        val settings = LyricCardSettings()
        assertThat(settings.textAlign).isEqualTo(LyricAlign.LEFT)
        assertThat(settings.textColor).isEqualTo("#ffffff")
        assertThat(settings.isBold).isFalse()
        assertThat(settings.backgroundColor).isEqualTo("#667eea")
        assertThat(settings.useSongImage).isTrue()
        assertThat(settings.overlayOpacity).isEqualTo(60)
        assertThat(settings.brightnessBoost).isEqualTo(0)
        assertThat(settings.fontFamily).isEqualTo(LyricCardFont.OPEN_SANS)
        assertThat(settings.fontSize).isEqualTo(14)
        assertThat(LyricCardSettings.EXPORT_SIZE_PX).isEqualTo(1800)
    }

    @Test
    fun unknownFontAndAlignFallBackToWebDefaults() {
        assertThat(LyricCardFont.fromStorage(null)).isEqualTo(LyricCardFont.OPEN_SANS)
        assertThat(LyricCardFont.fromStorage("Comic Sans")).isEqualTo(LyricCardFont.OPEN_SANS)
        assertThat(LyricCardFont.fromStorage("Lobster")).isEqualTo(LyricCardFont.LOBSTER)
        assertThat(LyricCardSettings.parseAlign(null)).isEqualTo(LyricAlign.LEFT)
        assertThat(LyricCardSettings.parseAlign("justify")).isEqualTo(LyricAlign.LEFT)
        assertThat(LyricCardSettings.parseAlign("center")).isEqualTo(LyricAlign.CENTER)
    }

    @Test
    fun clampsMatchWebSliderRanges() {
        assertThat(LyricCardSettings.clampOverlay(-10)).isEqualTo(0)
        assertThat(LyricCardSettings.clampOverlay(99)).isEqualTo(80)
        assertThat(LyricCardSettings.clampBrightness(-1)).isEqualTo(0)
        assertThat(LyricCardSettings.clampBrightness(100)).isEqualTo(60)
        assertThat(LyricCardSettings.clampFontSize(1)).isEqualTo(5)
        assertThat(LyricCardSettings.clampFontSize(80)).isEqualTo(40)
    }

    @Test
    fun overlayAlphasMatchWebCssPercent() {
        assertThat(LyricCardSettings.overlayAlphaByte(0)).isEqualTo(0)
        assertThat(LyricCardSettings.overlayAlphaByte(60)).isEqualTo(153)
        assertThat(LyricCardSettings.overlayAlphaByte(100)).isEqualTo(204)
        assertThat(LyricCardSettings.brightnessAlphaByte(0)).isEqualTo(0)
        assertThat(LyricCardSettings.brightnessAlphaByte(60)).isEqualTo(153)
    }

    @Test
    fun backgroundPresetsMatchWebSelect() {
        assertThat(LyricCardSettings.backgroundPresets.map { it.first })
            .containsExactly(
                "#667eea",
                "#601f9e",
                "#1a1a2e",
                "#0f3460",
                "#e94560",
                "#ff6b6b",
                "#4ecdc4",
                "#45b7d1",
            ).inOrder()
    }
}
