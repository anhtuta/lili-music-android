package com.anhtuta.lilimusic.core.ui.palette

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

data class CoverPaletteColors(
    val background: Color,
    val onBackground: Color,
)

object CoverPalette {
    private const val DEFAULT_RGB = 0xFF333333.toInt()
    private val DarkFallbackTop = Color(0xFF2C2540)
    private val DarkFallbackBottom = Color(0xFF121212)
    private val LightFallbackTop = Color(0xFFC5D8F0)
    private val LightFallbackBottom = Color(0xFFF6F3EE)

    fun colorsFromRgb(rgb: Int): CoverPaletteColors {
        val background = Color(rgb)
        val onBackground = if (isLight(background)) Color(0xFF1A1A1A) else Color.White
        return CoverPaletteColors(
            background = background.copy(alpha = 1f),
            onBackground = onBackground,
        )
    }

    fun fromBitmap(bitmap: Bitmap): CoverPaletteColors {
        val palette = Palette.from(bitmap).generate()
        val swatch = palette.dominantSwatch ?: palette.vibrantSwatch ?: palette.mutedSwatch
        return colorsFromRgb(swatch?.rgb ?: DEFAULT_RGB)
    }

    fun nowPlayingGradient(
        coverColor: Color?,
        darkTheme: Boolean,
    ): Brush {
        val bottom = if (darkTheme) DarkFallbackBottom else LightFallbackBottom
        val top =
            coverColor?.copy(alpha = 0.72f)
                ?: if (darkTheme) DarkFallbackTop else LightFallbackTop
        return Brush.verticalGradient(colors = listOf(top, bottom))
    }

    fun isLight(color: Color): Boolean {
        val luminance = (0.299f * color.red) + (0.587f * color.green) + (0.114f * color.blue)
        return luminance > 0.5f
    }
}
