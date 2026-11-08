package com.anhtuta.lilimusic.feature.downloads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhtuta.lilimusic.core.model.SongDownload

@Composable
fun DownloadsRoute(
    onBack: () -> Unit,
    onPlay: (SongDownload) -> Unit,
    viewModel: DownloadsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DownloadsScreen(
        state = state,
        callbacks =
            DownloadsCallbacks(
                onBack = onBack,
                onPlay = { item ->
                    viewModel.play(item)
                    onPlay(item)
                },
                onRetry = viewModel::retry,
                onCancel = viewModel::cancel,
                onDelete = viewModel::remove,
                onDeleteAllClick = viewModel::onDeleteAllClick,
                onConfirmDeleteAll = viewModel::onConfirmDeleteAll,
                onCancelDeleteAll = viewModel::onCancelDeleteAll,
            ),
    )
}
