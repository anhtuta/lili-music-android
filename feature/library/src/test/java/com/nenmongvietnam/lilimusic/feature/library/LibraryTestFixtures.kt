package com.nenmongvietnam.lilimusic.feature.library

import com.nenmongvietnam.lilimusic.core.model.LyricRef
import com.nenmongvietnam.lilimusic.core.model.Song

fun libraryTestSong(
    id: Long = 1L,
    title: String = "Hello",
    artist: String = "Artist",
    listens: Int = 10,
): Song =
    Song(
        id = id,
        title = title,
        artist = artist,
        album = "Album",
        path = "hello-$id",
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
