package com.anhtuta.lilimusic.feature.player

import com.anhtuta.lilimusic.core.data.repository.SongRepository
import com.anhtuta.lilimusic.core.datastore.PlaybackPreferenceState
import com.anhtuta.lilimusic.core.datastore.PlaybackPreferences
import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

fun playerTestSong(
    id: Long,
    title: String = "Song $id",
    path: String = "path-$id",
    artist: String = "Artist",
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
        streamUrl = "https://example.com/api/song/file?file=$id.mp3",
    )

class FakePlayerSongRepository : SongRepository {
    private val songs = MutableStateFlow<List<Song>>(emptyList())

    override fun observePlayableSongs(): Flow<List<Song>> = songs

    override fun observeSongByPath(path: String): Flow<Song?> = songs.map { list -> list.find { it.path == path } }

    override suspend fun refresh(
        sort: SongSort,
        order: SortOrder,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun recordListen(path: String): Result<Unit> = Result.success(Unit)

    fun emit(value: List<Song>) {
        songs.value = value
    }
}

class FakePlaybackPreferences(
    initial: PlaybackPreferenceState = PlaybackPreferenceState(),
) : PlaybackPreferences {
    private val state = MutableStateFlow(initial)
    override val preferences: Flow<PlaybackPreferenceState> = state

    override suspend fun setPlayMode(playMode: PlayMode) {
        state.value = state.value.copy(playMode = playMode)
    }

    override suspend fun setLastSongPath(path: String?) {
        state.value = state.value.copy(lastSongPath = path)
    }

    override suspend fun setSpeed(speed: Float) {
        state.value = state.value.copy(speed = speed)
    }
}
