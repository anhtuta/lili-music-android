package com.anhtuta.lilimusic.core.ui.theme

import androidx.compose.ui.graphics.Color
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricTheme

data class LyricThemeColors(
    val background: Color,
    val text: Color,
    val activeText: Color,
    val karaokeFill: Color,
    val overlay: Color,
    val sungText: Color,
)

private val KaraokeFillLight = Color(0xFF2199F9)
private val KaraokeFillDark = Color(0xFFFFCC00)
private val PictureOverlay = Color(0f, 0f, 0f, 0.4f)
private val LightSurface = Color(0xFFDFDFDF)
private val LightUnsung = Color(0xFF333333)
private val LightActive = Color(0xFF575757)
private val DarkSurface = Color(0xFF333333)
private val DarkUnsung = Color(0xFFDFDFDF)

fun LyricTheme.colors(
    appTheme: AppTheme = AppTheme.LIGHT,
    hasCover: Boolean = true,
): LyricThemeColors {
    val effectiveLight = isEffectiveLight(appTheme, hasCover)
    return when (this) {
        LyricTheme.DARK -> darkColors()
        LyricTheme.LIGHT -> lightColors()
        LyricTheme.PICTURE_BG ->
            if (hasCover) {
                val fill = karaokeFillFor(effectiveLight = false)
                LyricThemeColors(
                    background = Color.Transparent,
                    text = DarkUnsung,
                    activeText = Color.White,
                    karaokeFill = fill,
                    overlay = PictureOverlay,
                    sungText = fill,
                )
            } else if (effectiveLight) {
                lightColors()
            } else {
                darkColors()
            }
    }
}

fun LyricTheme.isEffectiveLight(
    appTheme: AppTheme,
    hasCover: Boolean,
): Boolean =
    when (this) {
        LyricTheme.LIGHT -> true
        LyricTheme.DARK -> false
        LyricTheme.PICTURE_BG -> !hasCover && appTheme == AppTheme.LIGHT
    }

private fun karaokeFillFor(effectiveLight: Boolean): Color = if (effectiveLight) KaraokeFillLight else KaraokeFillDark

private fun lightColors() =
    LyricThemeColors(
        background = LightSurface,
        text = LightUnsung,
        activeText = LightActive,
        karaokeFill = KaraokeFillLight,
        overlay = Color.Transparent,
        sungText = KaraokeFillLight,
    )

private fun darkColors() =
    LyricThemeColors(
        background = DarkSurface,
        text = DarkUnsung,
        activeText = Color.White,
        karaokeFill = KaraokeFillDark,
        overlay = Color.Transparent,
        sungText = KaraokeFillDark,
    )
