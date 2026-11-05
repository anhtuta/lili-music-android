package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.model.ParsedLyric

sealed class LyricLoadResult {
    data class Ready(
        val lyric: ParsedLyric,
    ) : LyricLoadResult()

    data object Missing : LyricLoadResult()

    data class Error(
        val throwable: Throwable,
    ) : LyricLoadResult()
}
