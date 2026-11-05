package com.anhtuta.lilimusic.core.model

data class LyricLine(
    val startMs: Long,
    val endMs: Long?,
    val words: List<LyricWord>,
    val translations: List<LyricTranslation> = emptyList(),
) {
    val isEmpty: Boolean
        get() = words.isEmpty() || words.all { it.isEmpty }
}
