package com.anhtuta.lilimusic.feature.library

import com.anhtuta.lilimusic.core.model.LyricRef
import com.anhtuta.lilimusic.core.model.Song

fun libraryTestSong(
    id: Long = 1L,
    title: String = "Hello",
    artist: String = "Artist",
    listens: Int = 10,
    path: String = "hello-$id",
): Song =
    Song(
        id = id,
        title = title,
        artist = artist,
        album = "Album",
        path = path,
        type = "Vietnam",
        listens = listens,
        fileName = "hello-$id.mp3",
        imageName = null,
        imageUrl = null,
        lyric = LyricRef("hello.lrc"),
        zingId = null,
        songOfTheYear = null,
        createdDate = "2021-06-09T12:21:29.000000Z",
        formatCreatedDate = "2021/06/09",
        streamUrl = "https://example.com/api/song/file?file=hello-$id.mp3",
    )
