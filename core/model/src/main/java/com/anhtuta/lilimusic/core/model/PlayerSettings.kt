package com.anhtuta.lilimusic.core.model

/**
 * Persisted player/lyric visuals. Defaults match the web first run
 * (`giganticLine` on via `div-res-big-active-line` on the lyric panel).
 */
data class PlayerSettings(
    val lyricAlign: LyricAlign = LyricAlign.CENTER,
    val lyricTheme: LyricTheme = LyricTheme.PICTURE_BG,
    val karaokeMode: Boolean = true,
    val blurMode: Boolean = false,
    val giganticLine: Boolean = true,
    val wordAppear: Boolean = false,
    val lineAppear: Boolean = false,
    val translate: Boolean = false,
    val appTheme: AppTheme = AppTheme.LIGHT,
)
