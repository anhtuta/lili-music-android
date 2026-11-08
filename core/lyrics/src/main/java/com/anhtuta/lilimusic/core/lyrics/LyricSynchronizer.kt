package com.anhtuta.lilimusic.core.lyrics

import com.anhtuta.lilimusic.core.model.LyricLine
import com.anhtuta.lilimusic.core.model.LyricMeta
import com.anhtuta.lilimusic.core.model.LyricTranslation
import com.anhtuta.lilimusic.core.model.ParsedLyric
import kotlin.math.ceil

data class LyricWordFrame(
    val text: String,
    val isEmpty: Boolean,
    val fill: Float,
    val countdownDots: Int,
    val isActive: Boolean,
    val isPassed: Boolean,
    val seekPositionMs: Long,
    val startMs: Long,
    val endMs: Long,
)

data class LyricLineFrame(
    val words: List<LyricWordFrame>,
    val translations: List<LyricTranslation>,
    val isActive: Boolean,
    val isPassed: Boolean,
    val seekPositionMs: Long,
)

data class LyricSyncFrame(
    val lines: List<LyricLineFrame>,
    val activeLineIndex: Int,
    val activeWordIndex: Int,
    val meta: LyricMeta,
    val userOffsetMs: Long,
    val combinedOffsetMs: Long,
)

object LyricSynchronizer {
    const val USER_OFFSET_STEP_MS = 100L

    fun sync(
        lyric: ParsedLyric,
        positionMs: Long,
        durationMs: Long,
        userOffsetMs: Long = 0L,
    ): LyricSyncFrame {
        val offsetMs = lyric.meta.offsetMs + userOffsetMs
        val indexed = flatten(lyric.lines, durationMs)
        val current = findCurrentWordIndex(indexed, positionMs, offsetMs)

        var wordCursor = 0
        var activeLineIndex = -1
        val lineFrames =
            lyric.lines.mapIndexed { lineIndex, line ->
                val frames =
                    line.words.map { word ->
                        val globalIndex = wordCursor
                        wordCursor += 1
                        val endMs = resolveEnd(word.endMs, durationMs)
                        val isActive = globalIndex == current
                        val isPassed = current >= 0 && globalIndex < current
                        val seekPositionMs = (word.startMs + offsetMs).coerceAtLeast(0L)
                        LyricWordFrame(
                            text = word.text,
                            isEmpty = word.isEmpty,
                            fill = fillFor(word.startMs, endMs, positionMs, offsetMs, isActive, isPassed),
                            countdownDots =
                                if (isActive && word.isEmpty && globalIndex < indexed.lastIndex) {
                                    countdownDots(word.startMs, endMs, positionMs, offsetMs)
                                } else {
                                    0
                                },
                            isActive = isActive,
                            isPassed = isPassed,
                            seekPositionMs = seekPositionMs,
                            startMs = word.startMs,
                            endMs = endMs,
                        )
                    }
                val isActive = frames.any { it.isActive }
                if (isActive) activeLineIndex = lineIndex
                LyricLineFrame(
                    words = frames,
                    translations = line.translations,
                    isActive = isActive,
                    isPassed = frames.isNotEmpty() && frames.all { it.isPassed },
                    seekPositionMs = (line.startMs + offsetMs).coerceAtLeast(0L),
                )
            }

        return LyricSyncFrame(
            lines = lineFrames,
            activeLineIndex = activeLineIndex,
            activeWordIndex = current,
            meta = lyric.meta,
            userOffsetMs = userOffsetMs,
            combinedOffsetMs = offsetMs,
        )
    }

    internal fun findCurrentWordIndex(
        words: List<TimedWord>,
        positionMs: Long,
        offsetMs: Long,
    ): Int {
        if (words.isEmpty()) return -1
        var lo = 0
        var hi = words.lastIndex
        var mid = 0
        while (lo <= hi) {
            mid = (lo + hi) / 2
            val start = words[mid].startMs + offsetMs
            val end = words[mid].endMs + offsetMs
            when {
                contains(start, end, positionMs) -> return preferLaterOverlap(words, mid, positionMs, offsetMs)
                start > positionMs -> hi = mid - 1
                else -> lo = mid + 1
            }
        }
        return if (mid == 0 || mid == words.lastIndex) -1 else mid
    }

    private fun preferLaterOverlap(
        words: List<TimedWord>,
        index: Int,
        positionMs: Long,
        offsetMs: Long,
    ): Int {
        val next = index + 1
        if (next > words.lastIndex) return index
        val nextStart = words[next].startMs + offsetMs
        val nextEnd = words[next].endMs + offsetMs
        return if (contains(nextStart, nextEnd, positionMs)) next else index
    }

    private fun contains(
        startMs: Long,
        endMs: Long,
        positionMs: Long,
    ): Boolean = startMs <= positionMs && positionMs <= endMs

    internal fun countdownDots(
        startMs: Long,
        endMs: Long,
        positionMs: Long,
        offsetMs: Long,
    ): Int {
        val timeDiffMs = endMs - startMs
        val timeLeftMs = endMs + offsetMs - positionMs
        val inWindow =
            (timeDiffMs >= 5_000L && timeLeftMs <= 5_000L) ||
                (timeDiffMs >= 3_000L && timeLeftMs <= 3_000L)
        if (!inWindow || timeLeftMs <= 0L) return 0
        return ceil(timeLeftMs / 1000.0).toInt().coerceIn(0, 5)
    }

    fun karaokeFill(
        startMs: Long,
        endMs: Long,
        positionMs: Long,
        offsetMs: Long,
    ): Float {
        val start = startMs + offsetMs
        val end = endMs + offsetMs
        val span = (end - start).toFloat()
        if (span <= 0f) return 1f
        return ((positionMs - start).toFloat() / span).coerceIn(0f, 1f)
    }

    private fun fillFor(
        startMs: Long,
        endMs: Long,
        positionMs: Long,
        offsetMs: Long,
        isActive: Boolean,
        isPassed: Boolean,
    ): Float =
        when {
            isPassed -> 1f
            isActive -> karaokeFill(startMs, endMs, positionMs, offsetMs)
            else -> 0f
        }

    private fun flatten(
        lines: List<LyricLine>,
        durationMs: Long,
    ): List<TimedWord> =
        lines.flatMap { line ->
            line.words.map { word ->
                TimedWord(
                    startMs = word.startMs,
                    endMs = resolveEnd(word.endMs, durationMs),
                )
            }
        }

    private fun resolveEnd(
        endMs: Long?,
        durationMs: Long,
    ): Long = endMs ?: durationMs.coerceAtLeast(0L)

    data class TimedWord(
        val startMs: Long,
        val endMs: Long,
    )
}
