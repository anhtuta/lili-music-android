package com.anhtuta.lilimusic.feature.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhtuta.lilimusic.core.data.repository.LyricLoadResult
import com.anhtuta.lilimusic.core.data.repository.LyricRepository
import com.anhtuta.lilimusic.core.data.repository.SettingsRepository
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.playback.PlaybackState
import com.anhtuta.lilimusic.core.playback.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LyricsViewModel
    @Inject
    constructor(
        private val playerController: PlayerController,
        private val lyricRepository: LyricRepository,
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        private val userOffsetMs = MutableStateFlow(0L)
        private val loadResult = MutableStateFlow<LyricLoadResult?>(null)

        val uiState: StateFlow<LyricsUiState> =
            combine(
                playerController.state,
                loadResult,
                userOffsetMs,
                settingsRepository.settings,
            ) { playback, load, offset, settings ->
                toUiState(playback, load, offset, settings)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LyricsUiState(),
            )

        init {
            viewModelScope.launch {
                playerController.state
                    .map { playback ->
                        playback.currentSong?.path to playback.currentSong?.lyric?.fileName
                    }.distinctUntilChanged()
                    .collect { (path, fileName) ->
                        userOffsetMs.value = 0L
                        if (path == null) {
                            loadResult.value = null
                        } else {
                            loadResult.value = null
                            loadResult.value = lyricRepository.loadLyric(fileName)
                        }
                    }
            }
        }

        fun nudgeOffset(deltaMs: Long) {
            userOffsetMs.value += deltaMs
        }

        fun seekToLine(positionMs: Long) {
            playerController.seekTo(positionMs)
        }

        private fun toUiState(
            playback: PlaybackState,
            load: LyricLoadResult?,
            offset: Long,
            settings: PlayerSettings,
        ): LyricsUiState {
            val hasSong = playback.currentSong != null
            val coverUrl = playback.currentSong?.imageUrl
            if (!hasSong) {
                return LyricsUiState(settings = settings)
            }
            val base =
                LyricsUiState(
                    hasSong = true,
                    userOffsetMs = offset,
                    settings = settings,
                    coverUrl = coverUrl,
                )
            return when (load) {
                null -> base.copy(isLoading = true)
                LyricLoadResult.Missing -> base.copy(isMissing = true)
                is LyricLoadResult.Error -> base.copy(errorMessage = load.throwable.message)
                is LyricLoadResult.Ready ->
                    base.copy(
                        frame =
                            LyricSynchronizer.sync(
                                lyric = load.lyric,
                                positionMs = playback.positionMs,
                                durationMs = playback.durationMs,
                                userOffsetMs = offset,
                            ),
                    )
            }
        }
    }
