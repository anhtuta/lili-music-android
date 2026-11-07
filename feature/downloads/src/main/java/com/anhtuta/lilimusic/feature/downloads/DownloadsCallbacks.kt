package com.anhtuta.lilimusic.feature.downloads

import com.anhtuta.lilimusic.core.model.SongDownload

data class DownloadsCallbacks(
    val onBack: () -> Unit,
    val onPlay: (SongDownload) -> Unit,
    val onRetry: (SongDownload) -> Unit,
    val onCancel: (SongDownload) -> Unit = {},
    val onDelete: (SongDownload) -> Unit,
    val onDeleteAllClick: () -> Unit,
    val onConfirmDeleteAll: () -> Unit,
    val onCancelDeleteAll: () -> Unit,
)
