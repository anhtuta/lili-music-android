package com.anhtuta.lilimusic.feature.lyrics

import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme

object LyricsTestTags {
    const val PANE = "lyrics-pane"
    const val FETCHING = "lyrics-fetching"
    const val MISSING = "lyrics-missing"
    const val MISSING_MAIL = "lyrics-missing-mail"
    const val MISSING_FB = "lyrics-missing-fb"
    const val ERROR = "lyrics-error"
    const val LINE = "lyrics-line"
    const val LINE_WORDS = "lyrics-line-words"
    const val ACTIVE_LINE = "lyrics-active-line"
    const val TRANSLATION = "lyrics-translation"
    const val OFFSET_VALUE = "lyrics-offset-value"
    const val OFFSET_EARLIER = "lyrics-offset-earlier"
    const val OFFSET_LATER = "lyrics-offset-later"
    const val COVER_BACKDROP = "lyrics-cover-backdrop"
    const val KARAOKE = "lyrics-karaoke"
    const val BLUR = "lyrics-blur"
    const val GIGANTIC = "lyrics-gigantic"
    const val WORD_APPEAR = "lyrics-word-appear"
    const val LINE_APPEAR = "lyrics-line-appear"
    const val TRANSLATE = "lyrics-translate"
    const val META_FOOTER = "lyrics-meta-footer"
    const val EDGE_FADE = "lyrics-edge-fade"
    const val OFFSET_ROW = "lyrics-offset-row"

    fun align(align: LyricAlign): String = "lyrics-align-${align.storageValue}"

    fun theme(theme: LyricTheme): String = "lyrics-theme-${theme.storageValue}"
}
