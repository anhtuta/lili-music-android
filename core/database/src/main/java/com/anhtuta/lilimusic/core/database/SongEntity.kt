package com.anhtuta.lilimusic.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anhtuta.lilimusic.core.model.LyricRef
import com.anhtuta.lilimusic.core.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String?,
    val path: String,
    val type: String?,
    val listens: Int,
    val fileName: String,
    val imageName: String?,
    val imageUrl: String?,
    val lyricFileName: String?,
    val zingId: String?,
    val songOfTheYear: Int?,
    val createdDate: String?,
    val formatCreatedDate: String?,
    val streamUrl: String,
)

fun SongEntity.toSong(): Song =
    Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        path = path,
        type = type,
        listens = listens,
        fileName = fileName,
        imageName = imageName,
        imageUrl = imageUrl,
        lyric = lyricFileName?.let(::LyricRef),
        zingId = zingId,
        songOfTheYear = songOfTheYear,
        createdDate = createdDate,
        formatCreatedDate = formatCreatedDate,
        streamUrl = streamUrl,
    )

fun Song.toEntity(): SongEntity =
    SongEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        path = path,
        type = type,
        listens = listens,
        fileName = fileName,
        imageName = imageName,
        imageUrl = imageUrl,
        lyricFileName = lyric?.fileName,
        zingId = zingId,
        songOfTheYear = songOfTheYear,
        createdDate = createdDate,
        formatCreatedDate = formatCreatedDate,
        streamUrl = streamUrl,
    )
