package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.lyricEdgeFade(fadeHeight: Dp = 24.dp): Modifier =
    graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val fadePx = fadeHeight.toPx()
            val stop = if (size.height <= 0f) 0f else (fadePx / size.height).coerceIn(0f, 0.45f)
            drawRect(
                brush =
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        stop to Color.Black,
                        1f - stop to Color.Black,
                        1f to Color.Transparent,
                    ),
                blendMode = BlendMode.DstIn,
            )
        }
