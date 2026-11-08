package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    state: LibraryUiState,
    onQueryChange: (String) -> Unit,
    onSortFieldChosen: (SongSort) -> Unit,
    onRefresh: () -> Unit,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {},
    onDownloadAction: (Song) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            LibraryContent(
                state = state,
                onQueryChange = onQueryChange,
                onSortFieldChosen = onSortFieldChosen,
                onRefresh = onRefresh,
                onSongClick = onSongClick,
                onDownloadAction = onDownloadAction,
                onSettingsClick = onSettingsClick,
                onDownloadsClick = onDownloadsClick,
            )
        }
    }
}

@Composable
private fun LibraryContent(
    state: LibraryUiState,
    onQueryChange: (String) -> Unit,
    onSortFieldChosen: (SongSort) -> Unit,
    onRefresh: () -> Unit,
    onSongClick: (Song) -> Unit,
    onDownloadAction: (Song) -> Unit,
    onSettingsClick: () -> Unit,
    onDownloadsClick: () -> Unit,
) {
    when {
        state.isLoading -> LibraryLoading()
        state.isOfflineError && state.songs.isEmpty() && state.query.isBlank() -> {
            LibraryError(onRetry = onRefresh)
        }
        else ->
            LibraryCatalog(
                state = state,
                onQueryChange = onQueryChange,
                onSortFieldChosen = onSortFieldChosen,
                onSongClick = onSongClick,
                onDownloadAction = onDownloadAction,
                onRefresh = onRefresh,
                onSettingsClick = onSettingsClick,
                onDownloadsClick = onDownloadsClick,
            )
    }
}

@Composable
private fun LibraryLoading() {
    val loadingDescription = stringResource(R.string.library_loading)
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .testTag(LibraryTestTags.LOADING)
                .semantics { contentDescription = loadingDescription },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LibraryError(onRetry: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .testTag(LibraryTestTags.ERROR)
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(R.string.library_error))
        LiliIconButton(
            icon = Icons.Filled.Refresh,
            label = stringResource(R.string.library_retry),
            onClick = onRetry,
            modifier =
                Modifier
                    .padding(top = 12.dp)
                    .testTag(LibraryTestTags.RETRY),
        )
    }
}

@Composable
private fun LibraryCatalog(
    state: LibraryUiState,
    onQueryChange: (String) -> Unit,
    onSortFieldChosen: (SongSort) -> Unit,
    onSongClick: (Song) -> Unit,
    onDownloadAction: (Song) -> Unit,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
    onDownloadsClick: () -> Unit,
) {
    val soyYears =
        remember(state.songs) {
            SongOfTheYearPalette.uniqueYearsDescending(state.songs.map { it.songOfTheYear })
        }
    var previewSong by remember { mutableStateOf<Song?>(null) }
    val listState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .testTag(LibraryTestTags.LIST),
            contentPadding =
                PaddingValues(
                    bottom = 8.dp,
                    end = LibraryChromeInsets.ScrollbarTrack,
                ),
        ) {
            if (state.isOfflineError) {
                item(key = "offline-banner") {
                    Text(
                        text = stringResource(R.string.library_error),
                        color = MaterialTheme.colorScheme.error,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .testTag(LibraryTestTags.ERROR_BANNER)
                                .padding(horizontal = LibraryChromeInsets.Horizontal, vertical = 8.dp),
                    )
                }
            }
            item(key = "header") {
                LibraryScrollableHeader(
                    onRefresh = onRefresh,
                    onDownloadsClick = onDownloadsClick,
                    onSettingsClick = onSettingsClick,
                )
            }
            item(key = "search") {
                LibrarySearchField(query = state.query, onQueryChange = onQueryChange)
            }
            item(key = "sort") {
                LibrarySortPill(
                    selected = state.sort,
                    sortOrder = state.sortOrder,
                    onSortFieldChosen = onSortFieldChosen,
                )
            }
            if (state.songs.isEmpty()) {
                item(key = "empty") {
                    LibraryEmpty(query = state.query)
                }
            } else {
                items(state.songs, key = { it.id }) { song ->
                    LibrarySongRow(
                        song = song,
                        soyYears = soyYears,
                        isNowPlaying = state.nowPlayingPath == song.path,
                        isPlaying = state.isPlaying,
                        coverUrl = state.coverUrl(song),
                        unavailableOffline = state.isUnavailableOffline(song.path),
                        downloadStatus = state.status(song.path),
                        downloadProgress = state.progress(song.path),
                        onClick = { onSongClick(song) },
                        onCoverClick = {
                            if (state.coverUrl(song) != null) {
                                previewSong = song
                            }
                        },
                        onDownloadAction = { onDownloadAction(song) },
                    )
                }
            }
        }
        LibraryLazyScrollbar(
            listState = listState,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
    previewSong?.let { preview ->
        CoverPreviewDialog(
            song = preview,
            coverUrl = state.coverUrl(preview),
            onDismiss = { previewSong = null },
        )
    }
}

@Composable
private fun LibraryEmpty(query: String) {
    val emptyText =
        if (query.isBlank()) {
            stringResource(R.string.library_empty)
        } else {
            stringResource(R.string.library_empty_search, query)
        }
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp)
                .testTag(LibraryTestTags.EMPTY),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emptyText)
    }
}
