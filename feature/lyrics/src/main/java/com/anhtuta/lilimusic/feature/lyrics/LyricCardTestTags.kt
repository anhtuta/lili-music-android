package com.anhtuta.lilimusic.feature.lyrics

object LyricCardTestTags {
    const val OPEN = "lyric-card-open"
    const val NO_LYRIC = "lyric-card-no-lyric"
    const val SELECTION = "lyric-card-selection"
    const val CONFIGURE = "lyric-card-configure"
    const val SELECT_ALL = "lyric-card-select-all"
    const val GENERATE = "lyric-card-generate"
    const val CLOSE = "lyric-card-close"
    const val BACK = "lyric-card-back"
    const val SHARE = "lyric-card-share"
    const val COPY = "lyric-card-copy"
    const val PREVIEW = "lyric-card-preview"
    const val SELECTED_PREVIEW = "lyric-card-selected-preview"
    const val BG_COLOR = "lyric-card-bg-color"
    const val BG_IMAGE = "lyric-card-bg-image"
    const val BOLD = "lyric-card-bold"
    const val FONT_SIZE = "lyric-card-font-size"
    const val OVERLAY = "lyric-card-overlay"
    const val BRIGHTNESS = "lyric-card-brightness"

    fun line(id: Int): String = "lyric-card-line-$id"

    fun align(value: String): String = "lyric-card-align-$value"

    fun font(value: String): String = "lyric-card-font-$value"

    fun backgroundPreset(hex: String): String = "lyric-card-bg-$hex"

    fun textColor(hex: String): String = "lyric-card-text-$hex"
}
