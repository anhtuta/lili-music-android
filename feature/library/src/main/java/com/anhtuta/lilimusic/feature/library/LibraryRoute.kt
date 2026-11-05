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
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LibraryScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onSortSelected = viewModel::onSortSelected,
        onSortOrderToggle = viewModel::onSortOrderToggle,
        onRefresh = viewModel::onRefresh,
        onSongClick = { song ->
            viewModel.onSongClick(song)
            onSongClick(song)
        },
    )
}
