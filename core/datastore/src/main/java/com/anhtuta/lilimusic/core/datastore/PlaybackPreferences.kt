package com.anhtuta.lilimusic.core.datastore

import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.PlaybackSpeed

data class PlaybackPreferenceState(
    val playMode: PlayMode = PlayMode.SEQUENCE,
    val lastSongPath: String? = null,
    val speed: Float = PlaybackSpeed.DEFAULT,
)

object PlaybackPreferencesParser {
    fun parse(
        playMode: String?,
        lastSongPath: String?,
        speed: Float?,
    ): PlaybackPreferenceState =
        PlaybackPreferenceState(
            playMode = PlayMode.fromStorage(playMode),
            lastSongPath = lastSongPath?.takeIf { it.isNotBlank() },
            speed = speed?.let(PlaybackSpeed::coerce) ?: PlaybackSpeed.DEFAULT,
        )
}
