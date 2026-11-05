package com.anhtuta.lilimusic.feature.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhtuta.lilimusic.core.data.repository.LyricLoadResult
import com.anhtuta.lilimusic.core.data.repository.LyricRepository
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
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
    ) : ViewModel() {
        private val userOffsetMs = MutableStateFlow(0L)
        private val loadResult = MutableStateFlow<LyricLoadResult?>(null)

        val uiState: StateFlow<LyricsUiState> =
            combine(
                playerController.state,
                loadResult,
                userOffsetMs,
            ) { playback, load, offset ->
                toUiState(playback, load, offset)
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
        ): LyricsUiState {
            val hasSong = playback.currentSong != null
            if (!hasSong) {
                return LyricsUiState()
            }
            return when (load) {
                null ->
                    LyricsUiState(hasSong = true, isLoading = true, userOffsetMs = offset)
                LyricLoadResult.Missing ->
                    LyricsUiState(hasSong = true, isMissing = true, userOffsetMs = offset)
                is LyricLoadResult.Error ->
                    LyricsUiState(
                        hasSong = true,
                        errorMessage = load.throwable.message,
                        userOffsetMs = offset,
                    )
                is LyricLoadResult.Ready ->
                    LyricsUiState(
                        hasSong = true,
                        frame =
                            LyricSynchronizer.sync(
                                lyric = load.lyric,
                                positionMs = playback.positionMs,
                                durationMs = playback.durationMs,
                                userOffsetMs = offset,
                            ),
                        userOffsetMs = offset,
                    )
            }
        }
    }
