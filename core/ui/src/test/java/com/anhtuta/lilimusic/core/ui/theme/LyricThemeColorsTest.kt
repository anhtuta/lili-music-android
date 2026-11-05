package com.anhtuta.lilimusic.core.ui.theme

import androidx.compose.ui.graphics.Color
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LyricThemeColorsTest {
    @Test
    fun darkThemeUsesDarkBackgroundAndYellowKaraoke() {
        val colors = LyricTheme.DARK.colors()
        assertThat(colors.background).isEqualTo(Color(0xFF333333))
        assertThat(colors.text).isEqualTo(Color(0xFFDFDFDF))
        assertThat(colors.activeText).isEqualTo(Color.White)
        assertThat(colors.karaokeFill).isEqualTo(Color(0xFFFFCC00))
        assertThat(colors.overlay).isEqualTo(Color.Transparent)
    }

    @Test
    fun lightThemeUsesLightBackgroundAndBlueKaraoke() {
        val colors = LyricTheme.LIGHT.colors()
        assertThat(colors.background).isEqualTo(Color(0xFFDFDFDF))
        assertThat(colors.text).isEqualTo(Color(0xFF333333))
        assertThat(colors.activeText).isEqualTo(Color(0xFF575757))
        assertThat(colors.karaokeFill).isEqualTo(Color(0xFF2199F9))
    }

    @Test
    fun pictureBgIsTransparentWithDarkOverlay() {
        val colors = LyricTheme.PICTURE_BG.colors()
        assertThat(colors.background).isEqualTo(Color.Transparent)
        assertThat(colors.text).isEqualTo(Color(0xFFDFDFDF))
        assertThat(colors.activeText).isEqualTo(Color.White)
        assertThat(colors.karaokeFill).isEqualTo(Color(0xFFFFCC00))
        assertThat(colors.overlay.alpha).isWithin(0.01f).of(0.4f)
    }
}
