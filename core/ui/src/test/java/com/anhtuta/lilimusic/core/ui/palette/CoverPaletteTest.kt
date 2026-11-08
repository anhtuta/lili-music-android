package com.anhtuta.lilimusic.core.ui.palette

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CoverPaletteTest {
    @Test
    fun darkRgbGetsLightOnBackground() {
        val colors = CoverPalette.colorsFromRgb(0xFF112233.toInt())
        assertThat(CoverPalette.isLight(colors.background)).isFalse()
        assertThat(colors.onBackground).isEqualTo(Color.White)
        assertThat(colors.background.alpha).isEqualTo(1f)
    }

    @Test
    fun lightRgbGetsDarkOnBackground() {
        val colors = CoverPalette.colorsFromRgb(0xFFEEEEEE.toInt())
        assertThat(CoverPalette.isLight(colors.background)).isTrue()
        assertThat(colors.onBackground).isEqualTo(Color(0xFF1A1A1A))
    }

    @Test
    fun nowPlayingGradientNeverUsesTransparentOnlyFallback() {
        val dark = CoverPalette.nowPlayingGradient(coverColor = null, darkTheme = true)
        val light = CoverPalette.nowPlayingGradient(coverColor = null, darkTheme = false)
        val tinted = CoverPalette.nowPlayingGradient(coverColor = Color(0xFF112233), darkTheme = true)
        assertThat(dark).isNotEqualTo(light)
        assertThat(tinted).isNotEqualTo(light)
    }

    @Test
    fun solidBitmapYieldsMatchingPaletteBackground() {
        val bitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(AndroidColor.rgb(17, 34, 51))
        val colors = CoverPalette.fromBitmap(bitmap)
        assertThat(CoverPalette.isLight(colors.background)).isFalse()
        assertThat(colors.onBackground).isEqualTo(Color.White)
    }
}
