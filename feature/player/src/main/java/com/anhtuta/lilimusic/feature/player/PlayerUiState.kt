package com.anhtuta.lilimusic.feature.player

import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.playback.PlaybackState

data class PlayerUiState(
    val song: Song? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playMode: PlayMode = PlayMode.SEQUENCE,
    val speed: Float = PlaybackSpeed.DEFAULT,
    val reachedEnd: Boolean = false,
    val atStartOfQueue: Boolean = false,
)

fun PlaybackState.toPlayerUiState(): PlayerUiState =
    PlayerUiState(
        song = currentSong,
        isPlaying = isPlaying,
        positionMs = positionMs,
        durationMs = durationMs,
        playMode = playMode,
        speed = speed,
        reachedEnd = reachedEnd,
        atStartOfQueue = atStartOfQueue,
    )
