package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LyricsRoute(
    modifier: Modifier = Modifier,
    viewModel: LyricsViewModel = hiltViewModel(),
    onLyricCardClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LyricsPane(
        state = state,
        onNudgeOffset = viewModel::nudgeOffset,
        onSeekToLine = viewModel::seekToLine,
        onLyricCardClick = onLyricCardClick,
        modifier = modifier,
    )
}
