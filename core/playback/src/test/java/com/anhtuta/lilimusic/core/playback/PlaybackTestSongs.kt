package com.anhtuta.lilimusic.core.playback

import com.anhtuta.lilimusic.core.model.Song

fun playbackTestSong(
    id: Long,
    title: String = "Song $id",
    path: String = "path-$id",
    artist: String = "Artist",
    streamUrl: String = "https://example.com/api/song/file?file=$id.mp3",
    imageUrl: String? = "https://example.com/$id.jpg",
): Song =
    Song(
        id = id,
        title = title,
        artist = artist,
        album = "Album",
        path = path,
        type = "Vietnam",
        listens = 1,
        fileName = "$id.mp3",
        imageName = null,
        imageUrl = imageUrl,
        lyric = null,
        zingId = null,
        songOfTheYear = null,
        createdDate = null,
        formatCreatedDate = null,
        streamUrl = streamUrl,
    )
