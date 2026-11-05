package com.anhtuta.lilimusic.core.ui.theme

import androidx.compose.ui.graphics.Color
import com.anhtuta.lilimusic.core.model.LyricTheme

data class LyricThemeColors(
    val background: Color,
    val text: Color,
    val activeText: Color,
    val karaokeFill: Color,
    val overlay: Color,
)

private val KaraokeFillLight = Color(0xFF2199F9)
private val KaraokeFillDark = Color(0xFFFFCC00)
private val PictureOverlay = Color(0f, 0f, 0f, 0.4f)

fun LyricTheme.colors(): LyricThemeColors =
    when (this) {
        LyricTheme.DARK ->
            LyricThemeColors(
                background = Color(0xFF333333),
                text = Color(0xFFDFDFDF),
                activeText = Color.White,
                karaokeFill = KaraokeFillDark,
                overlay = Color.Transparent,
            )
        LyricTheme.LIGHT ->
            LyricThemeColors(
                background = Color(0xFFDFDFDF),
                text = Color(0xFF333333),
                activeText = Color(0xFF575757),
                karaokeFill = KaraokeFillLight,
                overlay = Color.Transparent,
            )
        LyricTheme.PICTURE_BG ->
            LyricThemeColors(
                background = Color.Transparent,
                text = Color(0xFFDFDFDF),
                activeText = Color.White,
                karaokeFill = KaraokeFillDark,
                overlay = PictureOverlay,
            )
    }
