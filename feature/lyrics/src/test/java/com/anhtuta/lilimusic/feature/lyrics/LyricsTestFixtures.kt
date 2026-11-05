package com.anhtuta.lilimusic.feature.lyrics

import com.anhtuta.lilimusic.core.data.repository.LyricLoadResult
import com.anhtuta.lilimusic.core.data.repository.LyricRepository
import com.anhtuta.lilimusic.core.lyrics.LyricParser
import com.anhtuta.lilimusic.core.model.LyricRef
import com.anhtuta.lilimusic.core.model.Song

fun lyricsTestSong(
    id: Long = 1L,
    title: String = "Alpha",
    path: String = "alpha",
    lyricFileName: String? = "song.lrc",
): Song =
    Song(
        id = id,
        title = title,
        artist = "Artist",
        album = "Album",
        path = path,
        type = "Vietnam",
        listens = 1,
        fileName = "$id.mp3",
        imageName = null,
        imageUrl = null,
        lyric = lyricFileName?.let(::LyricRef),
        zingId = null,
        songOfTheYear = null,
        createdDate = null,
        formatCreatedDate = null,
        streamUrl = "https://example.com/api/song/file?file=$id.mp3",
    )

fun sampleParsedLyric() =
    LyricParser.parse(
        """
        [ti:Alpha]
        [00:00.00]First
        [00:01.00]Second
        [tran1]Segundo
        """.trimIndent(),
        "song.xlrc",
    )

class FakeLyricRepository(
    var result: LyricLoadResult = LyricLoadResult.Missing,
) : LyricRepository {
    val requests = mutableListOf<String?>()

    override suspend fun loadLyric(fileName: String?): LyricLoadResult {
        requests += fileName
        return result
    }
}
