package com.anhtuta.lilimusic.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PlaybackPreferences {
    val preferences: Flow<PlaybackPreferenceState>

    suspend fun setPlayMode(playMode: PlayMode)

    suspend fun setLastSongPath(path: String?)

    suspend fun setSpeed(speed: Float)
}

@Singleton
class PlaybackPreferencesDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : PlaybackPreferences {
        override val preferences: Flow<PlaybackPreferenceState> =
            dataStore.data.map { prefs ->
                PlaybackPreferencesParser.parse(
                    playMode = prefs[PLAY_MODE],
                    lastSongPath = prefs[LAST_SONG_PATH],
                    speed = prefs[SPEED],
                )
            }

        override suspend fun setPlayMode(playMode: PlayMode) {
            dataStore.edit { prefs ->
                prefs[PLAY_MODE] = playMode.storageValue
            }
        }

        override suspend fun setLastSongPath(path: String?) {
            dataStore.edit { prefs ->
                if (path.isNullOrBlank()) {
                    prefs.remove(LAST_SONG_PATH)
                } else {
                    prefs[LAST_SONG_PATH] = path
                }
            }
        }

        override suspend fun setSpeed(speed: Float) {
            dataStore.edit { prefs ->
                prefs[SPEED] = PlaybackSpeed.coerce(speed)
            }
        }

        private companion object {
            val PLAY_MODE = stringPreferencesKey("playback_play_mode")
            val LAST_SONG_PATH = stringPreferencesKey("playback_last_song_path")
            val SPEED = floatPreferencesKey("playback_speed")
        }
    }
