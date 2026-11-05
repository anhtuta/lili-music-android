package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.ui.theme.LyricThemeColors
import com.anhtuta.lilimusic.core.ui.theme.colors

data class LyricVisualStyle(
    val settings: PlayerSettings,
    val colors: LyricThemeColors,
    val textAlign: TextAlign,
    val horizontal: Alignment.Horizontal,
)

fun PlayerSettings.toVisualStyle(): LyricVisualStyle =
    LyricVisualStyle(
        settings = this,
        colors = lyricTheme.colors(),
        textAlign =
            when (lyricAlign) {
                LyricAlign.LEFT -> TextAlign.Start
                LyricAlign.CENTER -> TextAlign.Center
                LyricAlign.RIGHT -> TextAlign.End
            },
        horizontal =
            when (lyricAlign) {
                LyricAlign.LEFT -> Alignment.Start
                LyricAlign.CENTER -> Alignment.CenterHorizontally
                LyricAlign.RIGHT -> Alignment.End
            },
    )
