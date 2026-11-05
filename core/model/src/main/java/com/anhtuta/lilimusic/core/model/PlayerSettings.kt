package com.anhtuta.lilimusic.core.model

/**
 * Persisted player/lyric visuals. Defaults match PLAN.md §3
 * (gigantic line off even though the web HTML starts with `div-res-big-active-line`).
 */
data class PlayerSettings(
    val lyricAlign: LyricAlign = LyricAlign.CENTER,
    val lyricTheme: LyricTheme = LyricTheme.PICTURE_BG,
    val karaokeMode: Boolean = true,
    val blurMode: Boolean = false,
    val giganticLine: Boolean = false,
    val wordAppear: Boolean = false,
    val lineAppear: Boolean = false,
    val translate: Boolean = false,
    val appTheme: AppTheme = AppTheme.LIGHT,
)
