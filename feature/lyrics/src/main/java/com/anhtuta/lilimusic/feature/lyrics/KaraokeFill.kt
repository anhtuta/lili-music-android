package com.anhtuta.lilimusic.feature.lyrics

import android.os.SystemClock
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import com.anhtuta.lilimusic.core.lyrics.LyricLineFrame
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.lyrics.LyricWordFrame
import com.anhtuta.lilimusic.core.lyrics.PlaybackPositionInterpolator
import kotlin.math.max
import kotlin.math.min

internal data class KaraokeClock(
    val sampledPositionMs: Long,
    val sampledAtElapsedMs: Long,
    val speed: Float,
    val isPlaying: Boolean,
    val offsetMs: Long,
)

internal data class KaraokeSpanLayout(
    val annotated: AnnotatedString,
    val activeRange: IntRange?,
    val activeWord: LyricWordFrame?,
)

@Composable
internal fun rememberKaraokeDisplayPositionState(
    clock: KaraokeClock,
    elapsedRealtimeMs: () -> Long = { SystemClock.elapsedRealtime() },
): MutableLongState {
    val state = remember { mutableLongStateOf(clock.sampledPositionMs) }
    LaunchedEffect(clock.sampledPositionMs, clock.sampledAtElapsedMs, clock.speed, clock.isPlaying) {
        val snapshot =
            PlaybackPositionInterpolator.Snapshot(
                positionMs = clock.sampledPositionMs,
                speed = clock.speed,
                isPlaying = clock.isPlaying,
                capturedAtElapsedMs = clock.sampledAtElapsedMs,
            )
        state.longValue = PlaybackPositionInterpolator.interpolate(snapshot, elapsedRealtimeMs())
        if (!clock.isPlaying) {
            return@LaunchedEffect
        }
        while (true) {
            withFrameNanos {
                state.longValue = PlaybackPositionInterpolator.interpolate(snapshot, elapsedRealtimeMs())
            }
        }
    }
    return state
}

@Composable
internal fun KaraokeLine(
    line: LyricLineFrame,
    isActive: Boolean,
    style: LyricVisualStyle,
    displayPosition: MutableLongState,
    offsetMs: Long,
    isPlaying: Boolean,
) {
    val fontSize =
        MaterialTheme.typography.bodyLarge.fontSize *
            if (style.settings.giganticLine && isActive) 1.8f else 1f
    val fillColor = style.colors.karaokeFill
    val sungColor = style.colors.sungText
    val restColor = if (isActive) style.colors.activeText else style.colors.text
    val layout =
        remember(lineStructureKey(line), isActive, style, fontSize, fillColor, sungColor, restColor) {
            buildKaraokeSpanLayout(line, isActive, style, fontSize, fillColor, sungColor, restColor)
        }
    val countdownDescription =
        line.words.firstOrNull { it.countdownDots > 0 }?.let { word ->
            stringResource(R.string.lyrics_countdown_cd, word.countdownDots)
        }
    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }
    Text(
        text = layout.annotated,
        textAlign = style.textAlign,
        softWrap = true,
        overflow = TextOverflow.Clip,
        onTextLayout = { textLayout = it },
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag(LyricsTestTags.LINE_WORDS)
                .karaokeWordFill(
                    enabled = isActive && style.settings.karaokeMode,
                    layout = layout,
                    textLayout = textLayout,
                    displayPosition = displayPosition,
                    offsetMs = offsetMs,
                    isPlaying = isPlaying,
                    fillColor = fillColor,
                ).then(
                    if (countdownDescription != null) {
                        Modifier.semantics { contentDescription = countdownDescription }
                    } else {
                        Modifier
                    },
                ),
    )
}

internal fun Modifier.karaokeWordFill(
    enabled: Boolean,
    layout: KaraokeSpanLayout,
    textLayout: TextLayoutResult?,
    displayPosition: MutableLongState,
    offsetMs: Long,
    isPlaying: Boolean,
    fillColor: Color,
): Modifier {
    if (!enabled) return this
    return graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val measured = textLayout
            val range = layout.activeRange
            val word = layout.activeWord
            if (measured != null && range != null && word != null) {
                val fill =
                    activeWordFill(
                        word = word,
                        displayPositionMs = displayPosition.longValue,
                        offsetMs = offsetMs,
                        isPlaying = isPlaying,
                    )
                drawActiveWordFill(measured, range, fill, fillColor, this)
            }
        }
}

internal fun activeWordFill(
    word: LyricWordFrame?,
    displayPositionMs: Long,
    offsetMs: Long,
    isPlaying: Boolean,
): Float {
    if (word == null) return 0f
    if (!isPlaying) return word.fill
    return LyricSynchronizer.karaokeFill(word.startMs, word.endMs, displayPositionMs, offsetMs)
}

internal fun lineStructureKey(line: LyricLineFrame): List<List<Any>> =
    line.words.map { word ->
        listOf(word.text, word.isEmpty, word.isActive, word.isPassed, word.countdownDots)
    }

internal fun buildKaraokeSpanLayout(
    line: LyricLineFrame,
    isActive: Boolean,
    style: LyricVisualStyle,
    fontSize: TextUnit,
    fillColor: Color,
    sungColor: Color,
    restColor: Color,
): KaraokeSpanLayout {
    val shadow =
        if (style.settings.blurMode) {
            Shadow(color = Color.White, offset = Offset.Zero, blurRadius = 8f)
        } else {
            null
        }
    var cursor = 0
    var activeRange: IntRange? = null
    var activeWord: LyricWordFrame? = null
    val annotated =
        buildAnnotatedString {
            line.words.forEach { word ->
                val displayText = karaokeDisplayText(word, style)
                if (displayText.isEmpty()) {
                    return@forEach
                }
                val start = cursor
                withStyle(
                    SpanStyle(
                        brush =
                            wordBrush(
                                word = word,
                                karaokeMode = style.settings.karaokeMode,
                                fillColor = fillColor,
                                sungColor = sungColor,
                                restColor = restColor,
                            ),
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        fontSize = fontSize,
                        shadow = shadow,
                    ),
                ) {
                    append(displayText)
                }
                cursor += displayText.length
                if (word.isActive) {
                    activeRange = start until cursor
                    activeWord = word
                }
            }
        }
    return KaraokeSpanLayout(annotated = annotated, activeRange = activeRange, activeWord = activeWord)
}

internal fun karaokeDisplayText(
    word: LyricWordFrame,
    style: LyricVisualStyle,
): String {
    val hideWord =
        (style.settings.wordAppear || style.settings.lineAppear) &&
            !word.isPassed &&
            !word.isActive
    return when {
        hideWord -> ""
        word.isEmpty && word.countdownDots > 0 -> "•".repeat(word.countdownDots)
        word.isEmpty -> " "
        else -> word.text
    }
}

internal fun wordBrush(
    word: LyricWordFrame,
    karaokeMode: Boolean,
    fillColor: Color,
    restColor: Color,
    sungColor: Color = fillColor.copy(alpha = 0.55f),
) = when {
    !karaokeMode && (word.isActive || word.isPassed) -> SolidColor(fillColor)
    !karaokeMode -> SolidColor(restColor)
    word.isPassed -> SolidColor(sungColor)
    else -> SolidColor(restColor)
}

internal fun drawActiveWordFill(
    layout: TextLayoutResult,
    range: IntRange,
    fill: Float,
    color: Color,
    drawScope: DrawScope,
) {
    if (fill <= 0f || range.isEmpty()) return
    val start = range.first
    val endExclusive = range.last + 1
    val startLine = layout.getLineForOffset(start)
    val endLine = layout.getLineForOffset((endExclusive - 1).coerceAtLeast(start))
    val segments = mutableListOf<FillSegment>()
    var totalWidth = 0f
    for (line in startLine..endLine) {
        val segment = fillSegment(layout, line, start, endExclusive) ?: continue
        segments += segment
        totalWidth += segment.width
    }
    if (totalWidth <= 0f) return
    var remaining = if (fill >= 1f) totalWidth else totalWidth * fill
    for (segment in segments) {
        if (remaining <= 0f) break
        val filledWidth = min(segment.width, remaining)
        drawScope.clipRect(
            segment.left,
            segment.top,
            segment.left + filledWidth,
            segment.bottom,
        ) {
            drawRect(
                color = color,
                topLeft = Offset(segment.left, segment.top),
                size = Size(width = filledWidth, height = segment.bottom - segment.top),
                blendMode = BlendMode.SrcAtop,
            )
        }
        remaining -= filledWidth
    }
}

private fun fillSegment(
    layout: TextLayoutResult,
    line: Int,
    rangeStart: Int,
    rangeEndExclusive: Int,
): FillSegment? {
    val from = max(rangeStart, layout.getLineStart(line))
    val to = min(rangeEndExclusive, layout.getLineEnd(line, visibleEnd = true))
    if (from >= to) return null
    val a = layout.getHorizontalPosition(from, true)
    val b = layout.getHorizontalPosition(to, true)
    val left = min(a, b)
    val right = max(a, b)
    if (right - left <= 0f) return null
    return FillSegment(
        left = left,
        top = layout.getLineTop(line),
        right = right,
        bottom = layout.getLineBottom(line),
    )
}

private data class FillSegment(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float get() = right - left
}
