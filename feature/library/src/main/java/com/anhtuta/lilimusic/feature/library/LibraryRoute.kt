package com.anhtuta.lilimusic.feature.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhtuta.lilimusic.core.model.Song

@Composable
fun LibraryRoute(
    onSongClick: (Song) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LibraryScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onSortFieldChosen = viewModel::onSortFieldChosen,
        onRefresh = viewModel::onRefresh,
        onSongClick = { song ->
            if (viewModel.onSongClick(song)) {
                onSongClick(song)
            }
        },
        onSettingsClick = onSettingsClick,
        onDownloadsClick = onDownloadsClick,
        onDownloadAction = viewModel::onDownloadAction,
    )
}
