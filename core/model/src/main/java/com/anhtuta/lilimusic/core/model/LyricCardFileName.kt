package com.anhtuta.lilimusic.core.model

/**
 * Web `LyricDownloadModal` download name: `lyric-card-{titleNoSpaces}-{artistNoSpaces}.jpg`.
 */
object LyricCardFileName {
    fun jpeg(
        title: String?,
        artist: String?,
    ): String {
        val titlePart = compact(title) ?: "untitled"
        val artistPart = compact(artist) ?: "unknown"
        return "lyric-card-$titlePart-$artistPart.jpg"
    }

    private fun compact(value: String?): String? {
        val stripped = value.orEmpty().replace(Regex("\\s+"), "")
        return stripped.takeIf { it.isNotEmpty() }
    }
}
