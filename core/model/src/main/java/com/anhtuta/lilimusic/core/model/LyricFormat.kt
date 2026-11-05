package com.anhtuta.lilimusic.core.model

enum class LyricFormat {
    LRC,
    XLRC,
    TRC,
    ;

    companion object {
        const val MISSING_BODY = "Lyric doesn't exist!"

        fun fromFileName(fileName: String): LyricFormat {
            val lower = fileName.lowercase()
            return when {
                lower.endsWith(".xlrc") -> XLRC
                lower.endsWith(".lrc") -> LRC
                else -> TRC
            }
        }

        fun isMissingBody(body: String): Boolean {
            val trimmed = body.trim()
            return trimmed.isEmpty() || trimmed == MISSING_BODY
        }
    }
}
