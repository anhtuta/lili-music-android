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
    val sung = karaokeFillFor(effectiveLight).copy(alpha = 0.55f)
    return when (this) {
        LyricTheme.DARK -> darkColors(sung)
        LyricTheme.LIGHT -> lightColors(sung)
        LyricTheme.PICTURE_BG ->
            if (hasCover) {
                LyricThemeColors(
                    background = Color.Transparent,
                    text = DarkUnsung,
                    activeText = Color.White,
                    karaokeFill = karaokeFillFor(effectiveLight = false),
                    overlay = PictureOverlay,
                    sungText = karaokeFillFor(effectiveLight = false).copy(alpha = 0.55f),
                )
            } else if (effectiveLight) {
                lightColors(sung)
            } else {
                darkColors(sung)
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

private fun lightColors(sung: Color) =
    LyricThemeColors(
        background = LightSurface,
        text = LightUnsung,
        activeText = LightActive,
        karaokeFill = KaraokeFillLight,
        overlay = Color.Transparent,
        sungText = sung,
    )

private fun darkColors(sung: Color) =
    LyricThemeColors(
        background = DarkSurface,
        text = DarkUnsung,
        activeText = Color.White,
        karaokeFill = KaraokeFillDark,
        overlay = Color.Transparent,
        sungText = sung,
    )
