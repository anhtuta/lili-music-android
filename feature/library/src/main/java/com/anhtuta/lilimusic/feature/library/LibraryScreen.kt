package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    state: LibraryUiState,
    onQueryChange: (String) -> Unit,
    onSortSelected: (SongSort) -> Unit,
    onSortOrderToggle: () -> Unit,
    onRefresh: () -> Unit,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {},
    onDownloadAction: (Song) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.library_title)) },
                actions = {
                    TextButton(
                        onClick = onDownloadsClick,
                        modifier = Modifier.testTag(LibraryTestTags.DOWNLOADS),
                    ) {
                        Text(stringResource(R.string.library_downloads))
                    }
                    TextButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.testTag(LibraryTestTags.SETTINGS),
                    ) {
                        Text(stringResource(R.string.library_settings))
                    }
                },
            )
        },
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
                onSortSelected = onSortSelected,
                onSortOrderToggle = onSortOrderToggle,
                onRefresh = onRefresh,
                onSongClick = onSongClick,
                onDownloadAction = onDownloadAction,
            )
        }
    }
}

@Composable
private fun LibraryContent(
    state: LibraryUiState,
    onQueryChange: (String) -> Unit,
    onSortSelected: (SongSort) -> Unit,
    onSortOrderToggle: () -> Unit,
    onRefresh: () -> Unit,
    onSongClick: (Song) -> Unit,
    onDownloadAction: (Song) -> Unit,
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
                onSortSelected = onSortSelected,
                onSortOrderToggle = onSortOrderToggle,
                onSongClick = onSongClick,
                onDownloadAction = onDownloadAction,
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
        Button(
            onClick = onRetry,
            modifier =
                Modifier
                    .padding(top = 12.dp)
                    .testTag(LibraryTestTags.RETRY),
        ) {
            Text(stringResource(R.string.library_retry))
        }
    }
}

@Composable
private fun LibraryCatalog(
    state: LibraryUiState,
    onQueryChange: (String) -> Unit,
    onSortSelected: (SongSort) -> Unit,
    onSortOrderToggle: () -> Unit,
    onSongClick: (Song) -> Unit,
    onDownloadAction: (Song) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (state.isOfflineError) {
            Text(
                text = stringResource(R.string.library_error),
                color = MaterialTheme.colorScheme.error,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(LibraryTestTags.ERROR_BANNER)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag(LibraryTestTags.SEARCH),
            singleLine = true,
            label = { Text(stringResource(R.string.library_search_hint)) },
        )
        SortRow(
            selected = state.sort,
            sortOrder = state.sortOrder,
            onSortSelected = onSortSelected,
            onSortOrderToggle = onSortOrderToggle,
        )
        if (state.songs.isEmpty()) {
            LibraryEmpty(query = state.query)
        } else {
            val soyYears =
                remember(state.songs) {
                    SongOfTheYearPalette.uniqueYearsDescending(state.songs.map { it.songOfTheYear })
                }
            var previewSong by remember { mutableStateOf<Song?>(null) }
            LazyColumn(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag(LibraryTestTags.LIST),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(state.songs, key = { it.id }) { song ->
                    LibrarySongRow(
                        song = song,
                        soyYears = soyYears,
                        coverUrl = state.coverUrl(song),
                        unavailableOffline = state.isUnavailableOffline(song.path),
                        downloadLabel = downloadActionLabel(state, song.path),
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
            previewSong?.let { preview ->
                CoverPreviewDialog(
                    song = preview,
                    coverUrl = state.coverUrl(preview),
                    onDismiss = { previewSong = null },
                )
            }
        }
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
                .fillMaxSize()
                .testTag(LibraryTestTags.EMPTY),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emptyText)
    }
}

@Composable
private fun SortRow(
    selected: SongSort,
    sortOrder: SortOrder,
    onSortSelected: (SongSort) -> Unit,
    onSortOrderToggle: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.library_sort_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(end = 8.dp),
        )
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(SongSort.entries, key = { it.name }) { sort ->
                FilterChip(
                    selected = sort == selected,
                    onClick = { onSortSelected(sort) },
                    label = { Text(stringResource(sort.labelRes())) },
                    modifier = Modifier.testTag(LibraryTestTags.sort(sort)),
                )
            }
        }
        TextButton(
            onClick = onSortOrderToggle,
            modifier = Modifier.testTag(LibraryTestTags.SORT_ORDER),
        ) {
            Text(
                text =
                    stringResource(
                        if (sortOrder == SortOrder.ASC) {
                            R.string.library_sort_order_asc
                        } else {
                            R.string.library_sort_order_desc
                        },
                    ),
            )
        }
    }
}

@Composable
private fun downloadActionLabel(
    state: LibraryUiState,
    path: String,
): String =
    when (state.status(path)) {
        DownloadStatus.NONE -> stringResource(R.string.library_download)
        DownloadStatus.QUEUED, DownloadStatus.DOWNLOADING -> stringResource(R.string.library_download_cancel)
        DownloadStatus.COMPLETED -> stringResource(R.string.library_download_delete)
        DownloadStatus.FAILED -> stringResource(R.string.library_download_retry)
    }

private fun SongSort.labelRes(): Int =
    when (this) {
        SongSort.TITLE -> R.string.library_sort_title
        SongSort.LISTENS -> R.string.library_sort_listens
        SongSort.SONG_OF_THE_YEAR -> R.string.library_sort_song_of_the_year
        SongSort.TYPE -> R.string.library_sort_type
        SongSort.CREATED_DATE -> R.string.library_sort_created_date
    }
