package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit

internal object LyricLineMetrics {
    const val WRAP_LINE_HEIGHT_MULTIPLIER = 1.4f

    val lineHeightStyle =
        LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        )

    fun lineHeight(fontSize: TextUnit): TextUnit = fontSize * WRAP_LINE_HEIGHT_MULTIPLIER

    fun textStyle(fontSize: TextUnit): TextStyle =
        TextStyle(
            lineHeight = lineHeight(fontSize),
            lineHeightStyle = lineHeightStyle,
        )
}
