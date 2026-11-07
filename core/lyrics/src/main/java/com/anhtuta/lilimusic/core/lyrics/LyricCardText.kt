package com.anhtuta.lilimusic.core.lyrics

import com.anhtuta.lilimusic.core.model.LyricLine
import com.anhtuta.lilimusic.core.model.ParsedLyric

/**
 * Plain lyric text for the lyric-card selection step.
 *
 * Web feeds `div_result.innerText` (rendered playground, including translations
 * when translate is on, plus `appendLyricMeta`). Android rebuilds that from
 * [ParsedLyric]: sung lines, optional `[tranN]` lines, then the meta footer.
 * Empty countdown slots are omitted (they are not useful on a static card).
 */
object LyricCardText {
    const val META_SEPARATOR = "-----------"
    const val CREATOR_PREFIX = "Lyric creator: "
    const val SOURCE_PREFIX = "Source: "
    const val DATE_PREFIX = "Date: "

    fun canCreate(lyric: ParsedLyric?): Boolean = lyric != null && sungLines(lyric).isNotEmpty()

    fun selectableLines(
        lyric: ParsedLyric,
        includeTranslations: Boolean,
    ): List<String> {
        val lines = mutableListOf<String>()
        lyric.lines.forEach { line ->
            val sung = lineText(line)
            if (sung.isNotEmpty()) {
                lines += sung
            }
            if (includeTranslations) {
                line.translations
                    .map { it.text.trim() }
                    .filter { it.isNotEmpty() }
                    .forEach { lines += it }
            }
        }
        val footer = LyricMetaFooter.content(lyric.meta)
        if (!footer.isEmpty) {
            lines += META_SEPARATOR
            footer.titleLine?.let { lines += it }
            footer.by?.let { lines += CREATOR_PREFIX + it }
            footer.source?.let { lines += SOURCE_PREFIX + it }
            footer.date?.let { lines += DATE_PREFIX + it }
        }
        return lines
    }

    fun plain(
        lyric: ParsedLyric,
        includeTranslations: Boolean,
    ): String = selectableLines(lyric, includeTranslations).joinToString("\n")

    private fun sungLines(lyric: ParsedLyric): List<String> = lyric.lines.map(::lineText).filter { it.isNotEmpty() }

    private fun lineText(line: LyricLine): String = line.words.joinToString(separator = "") { it.text }.trim()
}
