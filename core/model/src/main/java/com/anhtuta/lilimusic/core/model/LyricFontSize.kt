package com.anhtuta.lilimusic.core.model

/**
 * Five lyric text presets. [NORMAL] matches the pre–Phase 12 default size (level 2).
 */
enum class LyricFontSize(
    val storageValue: String,
    val scale: Float,
) {
    COMPACT("compact", 0.85f),
    NORMAL("normal", 1f),
    COMFORTABLE("comfortable", 1.15f),
    LARGE("large", 1.3f),
    EXTRA_LARGE("extra_large", 1.5f),
    ;

    companion object {
        fun fromStorage(raw: String?): LyricFontSize = entries.firstOrNull { it.storageValue == raw } ?: NORMAL
    }
}
