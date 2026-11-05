package com.anhtuta.lilimusic.feature.lyrics

import com.anhtuta.lilimusic.core.lyrics.LyricSyncFrame

data class LyricsUiState(
    val hasSong: Boolean = false,
    val isLoading: Boolean = false,
    val isMissing: Boolean = false,
    val errorMessage: String? = null,
    val frame: LyricSyncFrame? = null,
    val userOffsetMs: Long = 0L,
)
