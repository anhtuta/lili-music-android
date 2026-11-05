package com.anhtuta.lilimusic.core.lyrics

import com.anhtuta.lilimusic.core.model.LyricFormat
import com.anhtuta.lilimusic.core.model.LyricLine
import com.anhtuta.lilimusic.core.model.LyricMeta
import com.anhtuta.lilimusic.core.model.LyricTranslation
import com.anhtuta.lilimusic.core.model.LyricWord
import com.anhtuta.lilimusic.core.model.ParsedLyric

object LyricParser {
    private val timeTag = Regex("""\[(?:\d+:)?\d+:\d+\.\d+\]""")
    private val translationTag = Regex("""\[tran\d+\]""")
    private val trcWord = Regex("""<\d+>[^<]*""")
    private val digits = Regex("""\d+""")

    fun parse(
        body: String,
        fileName: String,
    ): ParsedLyric {
        val format = LyricFormat.fromFileName(fileName)
        val text = body.trimStart('\uFEFF').trim()
        if (LyricFormat.isMissingBody(text)) {
            return ParsedLyric.empty(format)
        }
        return when (format) {
            LyricFormat.TRC -> parseTrc(text)
            LyricFormat.LRC,
            LyricFormat.XLRC,
            -> parseLrc(text, format)
        }
    }

    private fun parseLrc(
        text: String,
        format: LyricFormat,
    ): ParsedLyric {
        val lineMap = linkedMapOf<Long, String>()
        val translations = mutableMapOf<String, MutableMap<Long, String>>()
        var lastStartMs: Long? = null
        var meta = LyricMeta()

        for (raw in text.lineSequence()) {
            val line = raw.trimEnd('\r')
            if (line.isBlank()) continue
            val times = timeTag.findAll(line).map { it.value }.toList()
            val translation = translationTag.find(line)
            when {
                times.isNotEmpty() -> {
                    val timeLength = times.sumOf { it.length }
                    val words = line.substring(timeLength.coerceAtMost(line.length))
                    times.forEach { stamp ->
                        val startMs = LyricTimeDecoder.decode(stamp)
                        lastStartMs = startMs
                        lineMap[startMs] = words
                    }
                }
                translation != null && lastStartMs != null -> {
                    val tag = translation.value.removeSurrounding("[", "]")
                    val translated = line.substring(translation.value.length)
                    translations.getOrPut(tag) { mutableMapOf() }[lastStartMs] = translated
                }
                else -> meta = readMeta(line, meta)
            }
        }

        val starts = lineMap.keys.sorted()
        val parsedLines =
            starts.mapIndexed { index, startMs ->
                val endMs = starts.getOrNull(index + 1)
                val textForLine = lineMap.getValue(startMs)
                LyricLine(
                    startMs = startMs,
                    endMs = endMs,
                    words =
                        listOf(
                            LyricWord(
                                startMs = startMs,
                                endMs = endMs,
                                text = textForLine,
                                isEmpty = textForLine.isEmpty(),
                            ),
                        ),
                    translations =
                        translations.mapNotNull { (tag, byStart) ->
                            byStart[startMs]?.let { LyricTranslation(tag, it) }
                        },
                )
            }
        return ParsedLyric(format = format, meta = meta, lines = parsedLines)
    }

    private fun parseTrc(text: String): ParsedLyric {
        val parsedLines = mutableListOf<LyricLine>()
        var meta = LyricMeta()

        for (raw in text.lineSequence()) {
            val line = raw.trimEnd('\r')
            if (line.isBlank()) continue
            val times = timeTag.findAll(line).map { it.value }.toList()
            if (times.isEmpty()) {
                meta = readMeta(line, meta)
            } else {
                parseTrcTimedLine(line, times.first())?.let { parsedLines += it }
            }
        }
        return ParsedLyric(format = LyricFormat.TRC, meta = meta, lines = parsedLines)
    }

    private fun parseTrcTimedLine(
        line: String,
        timeStamp: String,
    ): LyricLine? {
        val startLine = LyricTimeDecoder.decode(timeStamp)
        val rest = line.substring(timeStamp.length)
        val wordTokens = trcWord.findAll(rest).map { it.value }.toList()
        if (wordTokens.isEmpty()) return null

        var startWord = startLine
        val words = mutableListOf<LyricWord>()
        for (token in wordTokens) {
            val durationMs = digits.find(token)?.value?.toLongOrNull() ?: 0L
            val endWord = startWord + durationMs
            val wordText = token.replace(Regex("""<\d+>"""), "")
            val isEmpty = wordText.trim().isEmpty()
            words +=
                LyricWord(
                    startMs = startWord,
                    endMs = endWord,
                    text = if (isEmpty) "" else wordText,
                    isEmpty = isEmpty,
                )
            startWord = endWord
        }
        return LyricLine(
            startMs = words.first().startMs,
            endMs = words.last().endMs,
            words = words,
        )
    }

    private fun readMeta(
        line: String,
        meta: LyricMeta,
    ): LyricMeta {
        val end = line.lastIndexOf(']')
        if (end < 0) return meta
        return when {
            line.contains("offset:") -> {
                val after = line.substringAfter("offset:")
                val value = Regex("""-?\d+""").find(after)?.value?.toLongOrNull() ?: 0L
                meta.copy(offsetMs = value)
            }
            line.contains("[author:") -> meta.copy(by = line.substring("[author:".length, end).trim())
            line.contains("[by:") -> meta.copy(by = line.substring("[by:".length, end).trim())
            line.contains("[createdDate:") -> meta.copy(date = line.substring("[createdDate:".length, end).trim())
            line.contains("[date:") -> meta.copy(date = line.substring("[date:".length, end).trim())
            line.contains("[source:") -> meta.copy(source = line.substring("[source:".length, end).trim())
            line.contains("[ti:") -> meta.copy(title = line.substring("[ti:".length, end).trim())
            line.contains("[ar:") -> meta.copy(artist = line.substring("[ar:".length, end).trim())
            else -> meta
        }
    }
}
