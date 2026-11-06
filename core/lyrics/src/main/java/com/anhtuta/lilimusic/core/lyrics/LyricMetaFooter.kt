package com.anhtuta.lilimusic.core.lyrics

import com.anhtuta.lilimusic.core.model.LyricMeta
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Web `BaiHat.js` `appendLyricMeta`: title – artist, creator, source, date (vi-VN m/yyyy).
 */
object LyricMetaFooter {
    private val monthYear =
        DateTimeFormatter.ofPattern("M/yyyy", Locale.forLanguageTag("vi-VN"))

    data class Content(
        val titleLine: String? = null,
        val by: String? = null,
        val source: String? = null,
        val date: String? = null,
    ) {
        val isEmpty: Boolean
            get() = titleLine == null && by == null && source == null && date == null
    }

    fun content(meta: LyricMeta): Content =
        Content(
            titleLine =
                meta.title
                    ?.takeIf { it.isNotBlank() }
                    ?.let { title -> "$title - ${meta.artist.orEmpty()}" },
            by = meta.by?.takeIf { it.isNotBlank() },
            source = meta.source?.takeIf { it.isNotBlank() },
            date = meta.date?.takeIf { it.isNotBlank() }?.let(::formatDate),
        )

    fun formatDate(raw: String): String {
        val parsed = parseDate(raw.trim()) ?: return raw
        return parsed.format(monthYear)
    }

    private fun parseDate(raw: String): LocalDate? =
        runCatching { LocalDate.parse(raw) }.getOrNull()
            ?: runCatching { OffsetDateTime.parse(raw).toLocalDate() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(raw).toLocalDate() }.getOrNull()
}
