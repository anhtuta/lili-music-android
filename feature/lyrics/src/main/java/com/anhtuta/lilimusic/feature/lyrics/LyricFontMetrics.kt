package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import com.anhtuta.lilimusic.core.model.PlayerSettings

internal object LyricFontMetrics {
    @Composable
    fun bodyFontSize(
        typography: Typography,
        settings: PlayerSettings,
        giganticActive: Boolean,
    ): TextUnit {
        val base = typography.bodyLarge.fontSize * settings.lyricFontSize.scale
        return if (settings.giganticLine && giganticActive) base * 1.8f else base
    }

    fun translationStyle(bodyFontSize: TextUnit): TextStyle = LyricLineMetrics.textStyle(bodyFontSize * 0.85f)
}
