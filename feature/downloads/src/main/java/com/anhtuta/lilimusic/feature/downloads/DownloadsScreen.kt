package com.anhtuta.lilimusic.feature.downloads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.SongDownload

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    state: DownloadsUiState,
    callbacks: DownloadsCallbacks,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.testTag(DownloadsTestTags.SCREEN),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.downloads_title)) },
                navigationIcon = {
                    TextButton(
                        onClick = callbacks.onBack,
                        modifier = Modifier.testTag(DownloadsTestTags.BACK),
                    ) {
                        Text(stringResource(R.string.downloads_back))
                    }
                },
                actions = {
                    if (state.items.isNotEmpty()) {
                        TextButton(
                            onClick = callbacks.onDeleteAllClick,
                            modifier = Modifier.testTag(DownloadsTestTags.DELETE_ALL),
                        ) {
                            Text(stringResource(R.string.downloads_delete_all))
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        if (state.isEmpty) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .testTag(DownloadsTestTags.EMPTY),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stringResource(R.string.downloads_empty))
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .testTag(DownloadsTestTags.LIST),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(state.items, key = { it.songPath }) { item ->
                    DownloadRow(
                        item = item,
                        onPlay = { callbacks.onPlay(item) },
                        onRetry = { callbacks.onRetry(item) },
                        onDelete = { callbacks.onDelete(item) },
                    )
                }
            }
        }
    }
    if (state.confirmDeleteAll) {
        DeleteAllConfirmation(callbacks = callbacks)
    }
}

@Composable
private fun DeleteAllConfirmation(callbacks: DownloadsCallbacks) {
    AlertDialog(
        onDismissRequest = callbacks.onCancelDeleteAll,
        title = { Text(stringResource(R.string.downloads_delete_all)) },
        text = { Text(stringResource(R.string.downloads_delete_all_confirm)) },
        confirmButton = {
            TextButton(
                onClick = callbacks.onConfirmDeleteAll,
                modifier = Modifier.testTag(DownloadsTestTags.CONFIRM_DELETE_ALL),
            ) {
                Text(stringResource(R.string.downloads_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = callbacks.onCancelDeleteAll,
                modifier = Modifier.testTag(DownloadsTestTags.CANCEL_DELETE_ALL),
            ) {
                Text(stringResource(R.string.downloads_cancel))
            }
        },
    )
}

@Composable
private fun DownloadRow(
    item: SongDownload,
    onPlay: () -> Unit,
    onRetry: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag(DownloadsTestTags.row(item.songPath))
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = item.title, style = MaterialTheme.typography.titleMedium)
        Text(text = item.artist, style = MaterialTheme.typography.bodyMedium)
        Text(text = statusLabel(item), style = MaterialTheme.typography.bodySmall)
        if (item.status == DownloadStatus.DOWNLOADING) {
            LinearProgressIndicator(
                progress = { item.progressPercent / 100f },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(DownloadsTestTags.progress(item.songPath)),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (item.isComplete) {
                TextButton(
                    onClick = onPlay,
                    modifier = Modifier.testTag(DownloadsTestTags.play(item.songPath)),
                ) {
                    Text(stringResource(R.string.downloads_play))
                }
            }
            if (item.status == DownloadStatus.FAILED) {
                TextButton(
                    onClick = onRetry,
                    modifier = Modifier.testTag(DownloadsTestTags.retry(item.songPath)),
                ) {
                    Text(stringResource(R.string.downloads_retry))
                }
            }
            TextButton(
                onClick = onDelete,
                modifier = Modifier.testTag(DownloadsTestTags.delete(item.songPath)),
            ) {
                Text(stringResource(R.string.downloads_delete))
            }
        }
    }
}

@Composable
private fun statusLabel(item: SongDownload): String =
    when (item.status) {
        DownloadStatus.QUEUED -> stringResource(R.string.downloads_status_queued)
        DownloadStatus.DOWNLOADING -> stringResource(R.string.downloads_status_downloading, item.progressPercent)
        DownloadStatus.COMPLETED -> stringResource(R.string.downloads_status_completed)
        DownloadStatus.FAILED -> item.errorMessage ?: stringResource(R.string.downloads_status_failed)
        DownloadStatus.NONE -> ""
    }
