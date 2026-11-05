package com.anhtuta.lilimusic.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        callbacks =
            SettingsCallbacks(
                onBack = onBack,
                onLyricAlign = viewModel::setLyricAlign,
                onLyricTheme = viewModel::setLyricTheme,
                onKaraokeMode = viewModel::setKaraokeMode,
                onBlurMode = viewModel::setBlurMode,
                onWordAppear = viewModel::setWordAppear,
                onLineAppear = viewModel::setLineAppear,
                onGiganticLine = viewModel::setGiganticLine,
                onTranslate = viewModel::setTranslate,
                onAppTheme = viewModel::setAppTheme,
                onIncreaseSpeed = viewModel::increaseSpeed,
                onDecreaseSpeed = viewModel::decreaseSpeed,
            ),
    )
}
