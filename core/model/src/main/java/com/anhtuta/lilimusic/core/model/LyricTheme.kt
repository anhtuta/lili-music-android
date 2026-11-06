package com.anhtuta.lilimusic.core.model

/**
 * Web lyric playground theme (`Setting.js` key `theme`, class `lyric-pg-*`).
 * Separate from [AppTheme]. Default is `picture-bg`.
 */
enum class LyricTheme(
    val storageValue: String,
) {
    DARK("dark"),
    LIGHT("light"),
    PICTURE_BG("picture-bg"),
    ;

    companion object {
        fun fromStorage(value: String?): LyricTheme = entries.find { it.storageValue == value } ?: PICTURE_BG
    }
}
