package com.anhtuta.lilimusic.feature.settings

import com.anhtuta.lilimusic.core.model.LyricFontSize

object SettingsTestTags {
    const val SCREEN = "settings-screen"
    const val BACK = "settings-back"
    const val ALIGN_LEFT = "settings-align-left"
    const val ALIGN_CENTER = "settings-align-center"
    const val ALIGN_RIGHT = "settings-align-right"
    const val THEME_DARK = "settings-theme-dark"
    const val THEME_LIGHT = "settings-theme-light"
    const val THEME_PICTURE_BG = "settings-theme-picture-bg"
    const val KARAOKE = "settings-karaoke"
    const val BLUR = "settings-blur"
    const val WORD_APPEAR = "settings-word-appear"
    const val LINE_APPEAR = "settings-line-appear"
    const val GIGANTIC = "settings-gigantic"
    const val TRANSLATE = "settings-translate"
    const val KEEP_SCREEN_ON_WHILE_LYRICS = "settings-keep-screen-on-while-lyrics"
    const val KEEP_SCREEN_ON_WHILE_LYRICS_NOTE = "settings-keep-screen-on-while-lyrics-note"
    const val APP_THEME_LIGHT = "settings-app-theme-light"
    const val APP_THEME_DARK = "settings-app-theme-dark"
    const val SPEED_UP = "settings-speed-up"
    const val SPEED_DOWN = "settings-speed-down"
    const val SPEED_VALUE = "settings-speed-value"

    fun fontSize(size: LyricFontSize): String = "settings-font-${size.storageValue}"
}
