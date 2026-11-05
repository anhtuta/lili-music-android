package com.anhtuta.lilimusic.core.model

data class ParsedLyric(
    val format: LyricFormat,
    val meta: LyricMeta = LyricMeta(),
    val lines: List<LyricLine> = emptyList(),
) {
    val words: List<LyricWord>
        get() = lines.flatMap { it.words }

    val isEmpty: Boolean
        get() = lines.isEmpty()

    companion object {
        fun empty(format: LyricFormat): ParsedLyric = ParsedLyric(format = format)
    }
}
