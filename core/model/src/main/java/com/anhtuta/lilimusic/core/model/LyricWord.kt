package com.anhtuta.lilimusic.core.model

data class LyricWord(
    val startMs: Long,
    val endMs: Long?,
    val text: String,
    val isEmpty: Boolean,
)
