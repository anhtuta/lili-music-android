package com.anhtuta.lilimusic.feature.settings

import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import com.anhtuta.lilimusic.core.model.PlayerSettings

data class SettingsUiState(
    val settings: PlayerSettings = PlayerSettings(),
    val speed: Float = PlaybackSpeed.DEFAULT,
)
