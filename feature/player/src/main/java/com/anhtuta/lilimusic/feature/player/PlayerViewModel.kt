package com.anhtuta.lilimusic.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhtuta.lilimusic.core.data.repository.SongRepository
import com.anhtuta.lilimusic.core.datastore.PlaybackPreferences
import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import com.anhtuta.lilimusic.core.playback.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerViewModel
    @Inject
    constructor(
        private val playerController: PlayerController,
        private val songRepository: SongRepository,
        private val playbackPreferences: PlaybackPreferences,
    ) : ViewModel() {
        val uiState: StateFlow<PlayerUiState> =
            playerController.state
                .map { it.toPlayerUiState() }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = PlayerUiState(),
                )

        init {
            viewModelScope.launch { restoreLastPlayed() }
        }

        fun ensurePlaying(path: String) {
            viewModelScope.launch {
                if (playerController.state.value.currentSong
                        ?.path == path
                ) {
                    return@launch
                }
                val songs = songRepository.observePlayableSongs().first()
                if (songs.any { it.path == path }) {
                    playerController.play(songs, path)
                }
            }
        }

        fun playPause() {
            playerController.playPause()
        }

        fun skipToNext() {
            playerController.skipToNext()
        }

        fun skipToPrevious() {
            playerController.skipToPrevious()
        }

        fun seekTo(positionMs: Long) {
            playerController.seekTo(positionMs)
        }

        fun seekBack() {
            playerController.seekBack()
        }

        fun seekForward() {
            playerController.seekForward()
        }

        fun toggleShuffle() {
            playerController.setPlayMode(
                playerController.state.value.playMode
                    .toggleShuffle(),
            )
        }

        fun toggleRepeatOne() {
            playerController.setPlayMode(
                playerController.state.value.playMode
                    .toggleRepeatOne(),
            )
        }

        fun increaseSpeed() {
            playerController.setSpeed(PlaybackSpeed.increase(playerController.state.value.speed))
        }

        fun decreaseSpeed() {
            playerController.setSpeed(PlaybackSpeed.decrease(playerController.state.value.speed))
        }

        private suspend fun restoreLastPlayed() {
            if (playerController.state.value.currentSong != null) return
            val prefs = playbackPreferences.preferences.first()
            val path = prefs.lastSongPath ?: return
            val songs = songRepository.observePlayableSongs().first { catalog -> catalog.any { it.path == path } }
            if (playerController.state.value.currentSong != null) return
            playerController.setPlayMode(prefs.playMode)
            playerController.setSpeed(prefs.speed)
            playerController.prepare(songs, path)
        }
    }
