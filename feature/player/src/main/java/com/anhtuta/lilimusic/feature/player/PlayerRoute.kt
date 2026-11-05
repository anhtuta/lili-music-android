package com.anhtuta.lilimusic.feature.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PlayerRoute(
    path: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
    lyrics: @Composable () -> Unit = {},
) {
    LaunchedEffect(path) {
        viewModel.ensurePlaying(path)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PlayerScreen(
        state = state,
        callbacks =
            PlayerCallbacks(
                onBack = onBack,
                onPlayPause = viewModel::playPause,
                onSkipNext = viewModel::skipToNext,
                onSkipPrevious = viewModel::skipToPrevious,
                onSeek = viewModel::seekTo,
                onToggleShuffle = viewModel::toggleShuffle,
                onToggleRepeatOne = viewModel::toggleRepeatOne,
                onIncreaseSpeed = viewModel::increaseSpeed,
                onDecreaseSpeed = viewModel::decreaseSpeed,
            ),
        lyrics = lyrics,
    )
}
