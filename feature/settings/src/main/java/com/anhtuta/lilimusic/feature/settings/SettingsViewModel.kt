package com.anhtuta.lilimusic.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhtuta.lilimusic.core.data.repository.SettingsRepository
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricFontSize
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import com.anhtuta.lilimusic.core.playback.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
        private val playerController: PlayerController,
    ) : ViewModel() {
        val uiState: StateFlow<SettingsUiState> =
            combine(
                settingsRepository.settings,
                playerController.state,
            ) { settings, playback ->
                SettingsUiState(settings = settings, speed = playback.speed)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState(),
            )

        fun setLyricAlign(align: LyricAlign) {
            viewModelScope.launch { settingsRepository.setLyricAlign(align) }
        }

        fun setLyricTheme(theme: LyricTheme) {
            viewModelScope.launch { settingsRepository.setLyricTheme(theme) }
        }

        fun setLyricFontSize(size: LyricFontSize) {
            viewModelScope.launch { settingsRepository.setLyricFontSize(size) }
        }

        fun setKaraokeMode(enabled: Boolean) {
            viewModelScope.launch { settingsRepository.setKaraokeMode(enabled) }
        }

        fun setBlurMode(enabled: Boolean) {
            viewModelScope.launch { settingsRepository.setBlurMode(enabled) }
        }

        fun setGiganticLine(enabled: Boolean) {
            viewModelScope.launch { settingsRepository.setGiganticLine(enabled) }
        }

        fun setWordAppear(enabled: Boolean) {
            viewModelScope.launch { settingsRepository.setWordAppear(enabled) }
        }

        fun setLineAppear(enabled: Boolean) {
            viewModelScope.launch { settingsRepository.setLineAppear(enabled) }
        }

        fun setTranslate(enabled: Boolean) {
            viewModelScope.launch { settingsRepository.setTranslate(enabled) }
        }

        fun setAppTheme(theme: AppTheme) {
            viewModelScope.launch { settingsRepository.setAppTheme(theme) }
        }

        fun increaseSpeed() {
            playerController.setSpeed(PlaybackSpeed.increase(playerController.state.value.speed))
        }

        fun decreaseSpeed() {
            playerController.setSpeed(PlaybackSpeed.decrease(playerController.state.value.speed))
        }
    }
