package com.anhtuta.lilimusic.core.ui.text

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    scrollState: ScrollState = rememberScrollState(),
) {
    val density = LocalDensity.current
    val dragging by scrollState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(text) {
        scrollState.scrollTo(0)
    }
    LaunchedEffect(text, scrollState.maxValue, dragging) {
        if (!MarqueeMetrics.shouldAnimate(scrollState.maxValue, dragging)) {
            return@LaunchedEffect
        }
        val initialPause =
            if (scrollState.value == 0) {
                MarqueeMetrics.START_PAUSE_MS
            } else {
                MarqueeMetrics.RESUME_PAUSE_MS
            }
        delay(initialPause)
        val speedPx = with(density) { MarqueeMetrics.SPEED_DP_PER_SEC.dp.toPx() }
        while (true) {
            val remaining = (scrollState.maxValue - scrollState.value).coerceAtLeast(0)
            val duration = MarqueeMetrics.durationMs(remaining.toFloat(), speedPx)
            if (duration > 0) {
                scrollState.animateScrollTo(
                    scrollState.maxValue,
                    tween(durationMillis = duration, easing = LinearEasing),
                )
            }
            delay(MarqueeMetrics.END_PAUSE_MS)
            scrollState.scrollTo(0)
            delay(MarqueeMetrics.START_PAUSE_MS)
        }
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            color = color,
            style = style,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = modifier.horizontalScroll(state = scrollState),
        )
    }
}
