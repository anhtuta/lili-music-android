package com.anhtuta.lilimusic.core.playback

import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.Song
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {
    val state: StateFlow<PlaybackState>

    fun play(
        queue: List<Song>,
        startPath: String,
    )

    fun prepare(
        queue: List<Song>,
        startPath: String,
    )

    fun playPause()

    fun skipToNext()

    fun skipToPrevious()

    fun seekTo(positionMs: Long)

    fun setPlayMode(playMode: PlayMode)

    fun setSpeed(speed: Float)
}
