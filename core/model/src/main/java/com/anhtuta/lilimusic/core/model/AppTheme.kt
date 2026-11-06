package com.anhtuta.lilimusic.core.model

/**
 * Web chrome theme (`SETTING_APP_THEME` / `appTheme`). Separate from [LyricTheme].
 * Default is light.
 */
enum class AppTheme(
    val storageValue: String,
) {
    LIGHT("light"),
    DARK("dark"),
    ;

    companion object {
        fun fromStorage(value: String?): AppTheme = entries.find { it.storageValue == value } ?: LIGHT
    }
}
