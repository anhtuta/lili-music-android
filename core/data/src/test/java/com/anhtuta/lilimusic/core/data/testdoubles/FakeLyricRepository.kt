package com.anhtuta.lilimusic.core.data.testdoubles

import com.anhtuta.lilimusic.core.data.repository.LyricLoadResult
import com.anhtuta.lilimusic.core.data.repository.LyricRepository

class FakeLyricRepository : LyricRepository {
    val loaded = mutableListOf<String?>()
    var result: LyricLoadResult = LyricLoadResult.Missing
    var error: Throwable? = null

    override suspend fun loadLyric(fileName: String?): LyricLoadResult {
        loaded += fileName
        error?.let { throw it }
        return result
    }
}
