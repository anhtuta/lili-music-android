package com.anhtuta.lilimusic.core.ui.palette

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

data class CoverPaletteColors(
    val background: Color,
    val onBackground: Color,
)

object CoverPalette {
    private const val DEFAULT_RGB = 0xFF333333.toInt()

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

    internal fun isLight(color: Color): Boolean {
        val luminance = (0.299f * color.red) + (0.587f * color.green) + (0.114f * color.blue)
        return luminance > 0.5f
    }
}
