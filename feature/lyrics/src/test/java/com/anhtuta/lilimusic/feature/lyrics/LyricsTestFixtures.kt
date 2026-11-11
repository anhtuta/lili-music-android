package com.anhtuta.lilimusic.feature.lyrics

import com.anhtuta.lilimusic.core.data.repository.LyricLoadResult
import com.anhtuta.lilimusic.core.data.repository.LyricRepository
import com.anhtuta.lilimusic.core.data.repository.SettingsRepository
import com.anhtuta.lilimusic.core.lyrics.LyricParser
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricRef
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

fun lyricsTestSong(
    id: Long = 1L,
    title: String = "Alpha",
    path: String = "alpha",
    lyricFileName: String? = "song.lrc",
    imageUrl: String? = null,
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
        imageUrl = imageUrl,
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
    val deleted = mutableListOf<String?>()

    override suspend fun loadLyric(fileName: String?): LyricLoadResult {
        requests += fileName
        return result
    }

    override suspend fun deleteCached(fileName: String?) {
        deleted += fileName
    }
}

class FakeSettingsRepository(
    initial: PlayerSettings = PlayerSettings(),
) : SettingsRepository {
    private val state = MutableStateFlow(initial)
    var writeCount = 0
        private set
    override val settings: Flow<PlayerSettings> = state

    override suspend fun setLyricAlign(align: LyricAlign) {
        writeCount += 1
        state.value = state.value.copy(lyricAlign = align)
    }

    override suspend fun setLyricTheme(theme: LyricTheme) {
        state.value = state.value.copy(lyricTheme = theme)
    }

    override suspend fun setKaraokeMode(enabled: Boolean) {
        state.value = state.value.copy(karaokeMode = enabled)
    }

    override suspend fun setBlurMode(enabled: Boolean) {
        state.value = state.value.copy(blurMode = enabled)
    }

    override suspend fun setGiganticLine(enabled: Boolean) {
        state.value = state.value.copy(giganticLine = enabled)
    }

    override suspend fun setWordAppear(enabled: Boolean) {
        state.value = state.value.copy(wordAppear = enabled)
    }

    override suspend fun setLineAppear(enabled: Boolean) {
        state.value = state.value.copy(lineAppear = enabled)
    }

    override suspend fun setTranslate(enabled: Boolean) {
        state.value = state.value.copy(translate = enabled)
    }

    override suspend fun setAppTheme(theme: AppTheme) {
        state.value = state.value.copy(appTheme = theme)
    }

    override suspend fun setLyricFontSize(size: com.anhtuta.lilimusic.core.model.LyricFontSize) {
        state.value = state.value.copy(lyricFontSize = size)
    }

    override suspend fun setKeepScreenOnWhileLyrics(enabled: Boolean) {
        state.value = state.value.copy(keepScreenOnWhileLyrics = enabled)
    }

    fun emit(value: PlayerSettings) {
        state.value = value
    }
}
