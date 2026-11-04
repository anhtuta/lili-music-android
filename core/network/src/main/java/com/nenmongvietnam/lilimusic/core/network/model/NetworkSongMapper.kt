package com.nenmongvietnam.lilimusic.core.network.model

import com.nenmongvietnam.lilimusic.core.model.ImageUrlResolver
import com.nenmongvietnam.lilimusic.core.model.LyricRef
import com.nenmongvietnam.lilimusic.core.model.Song
import com.nenmongvietnam.lilimusic.core.model.SongCatalog
import com.nenmongvietnam.lilimusic.core.model.SongStreamUrl

fun NetworkSong.toPlayableSong(baseUrl: String): Song? {
    if (!SongCatalog.isPlayableFileName(fileName)) return null
    val playableFileName = checkNotNull(fileName)
    return Song(
        id = id,
        title = title,
        artist = artist.orEmpty(),
        album = album,
        path = path,
        type = type,
        listens = listens,
        fileName = playableFileName,
        imageName = imageName,
        imageUrl = ImageUrlResolver.resolve(imageUrl, baseUrl),
        lyric = lyric?.takeIf { it.isNotBlank() }?.let(::LyricRef),
        zingId = zingId,
        songOfTheYear = songOfTheYear,
        createdDate = createdDate,
        formatCreatedDate = formatCreatedDate,
        streamUrl = SongStreamUrl.build(baseUrl, playableFileName),
    )
}
