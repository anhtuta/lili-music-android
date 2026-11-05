package com.anhtuta.lilimusic.core.model

/**
 * Web `lyricAlign` values applied to `#div_result`. Default is center (web inline style).
 */
enum class LyricAlign(
    val storageValue: String,
) {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right"),
    ;

    companion object {
        fun fromStorage(value: String?): LyricAlign = entries.find { it.storageValue == value } ?: CENTER
    }
}
