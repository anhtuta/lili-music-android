package com.anhtuta.lilimusic.feature.player

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NowPlayingChromeColorsTest {
    @Test
    fun darkThemeForcesWhiteControlsRegardlessOfPaletteContrast() {
        val color =
            NowPlayingChromeColors.controlsOnSurface(
                darkTheme = true,
                themeOnSurface = Color(0xFF1A1A1A),
            )
        assertThat(color).isEqualTo(Color.White)
    }

    @Test
    fun lightThemeUsesMaterialOnSurface() {
        val onSurface = Color(0xFF1A1A1A)
        val color =
            NowPlayingChromeColors.controlsOnSurface(
                darkTheme = false,
                themeOnSurface = onSurface,
            )
        assertThat(color).isEqualTo(onSurface)
    }
}
