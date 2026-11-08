package com.anhtuta.lilimusic.feature.library

import com.anhtuta.lilimusic.core.model.Song

data class SongInfoRow(
    val label: String,
    val value: String,
)

object SongInfoFields {
    const val NONE = "None"

    fun display(
        raw: String?,
        none: String = NONE,
    ): String = raw?.takeIf { it.isNotBlank() } ?: none

    fun display(
        raw: Int?,
        none: String = NONE,
    ): String = raw?.toString() ?: none

    fun lyricType(
        fileName: String?,
        none: String = NONE,
    ): String {
        if (fileName.isNullOrBlank()) return none
        val ext = fileName.substringAfterLast('.', missingDelimiterValue = "")
        return ext.takeIf { it.isNotBlank() }?.lowercase() ?: none
    }

    fun rows(
        song: Song,
        labels: SongInfoLabels,
        none: String = NONE,
    ): List<SongInfoRow> =
        listOf(
            SongInfoRow(labels.title, display(song.title, none)),
            SongInfoRow(labels.artist, display(song.artist, none)),
            SongInfoRow(labels.album, display(song.album, none)),
            SongInfoRow(labels.type, display(song.type, none)),
            SongInfoRow(labels.songOfTheYear, display(song.songOfTheYear, none)),
            SongInfoRow(labels.lyricType, lyricType(song.lyric?.fileName, none)),
            SongInfoRow(labels.lyricFile, display(song.lyric?.fileName, none)),
            SongInfoRow(labels.listens, song.listens.toString()),
            SongInfoRow(labels.date, display(song.formatCreatedDate, none)),
            SongInfoRow(labels.createdDate, display(song.createdDate, none)),
            SongInfoRow(labels.fileName, display(song.fileName, none)),
            SongInfoRow(labels.path, display(song.path, none)),
            SongInfoRow(labels.id, song.id.toString()),
            SongInfoRow(labels.zingId, display(song.zingId, none)),
            SongInfoRow(labels.imageName, display(song.imageName, none)),
            SongInfoRow(labels.imageUrl, display(song.imageUrl, none)),
            SongInfoRow(labels.streamUrl, display(song.streamUrl, none)),
        )
}

data class SongInfoLabels(
    val title: String,
    val artist: String,
    val album: String,
    val type: String,
    val songOfTheYear: String,
    val lyricType: String,
    val lyricFile: String,
    val listens: String,
    val date: String,
    val createdDate: String,
    val fileName: String,
    val path: String,
    val id: String,
    val zingId: String,
    val imageName: String,
    val imageUrl: String,
    val streamUrl: String,
)
