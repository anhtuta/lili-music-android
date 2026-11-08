package com.anhtuta.lilimusic.core.ui.download

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.ui.R
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton

@Composable
fun DownloadActionButton(
    status: DownloadStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    progressPercent: Int = 0,
) {
    val description = stringResource(descriptionRes(status))
    LiliIconButton(
        label = description,
        onClick = onClick,
        modifier = modifier,
    ) {
        DownloadActionVisual(status = status, progressPercent = progressPercent)
    }
}

private fun descriptionRes(status: DownloadStatus): Int =
    when (status) {
        DownloadStatus.NONE -> R.string.download_action_download
        DownloadStatus.QUEUED, DownloadStatus.DOWNLOADING -> R.string.download_action_cancel
        DownloadStatus.COMPLETED -> R.string.download_action_delete
        DownloadStatus.FAILED -> R.string.download_action_retry
    }

@Composable
private fun DownloadActionVisual(
    status: DownloadStatus,
    progressPercent: Int,
) {
    val indicatorModifier = Modifier.size(24.dp)
    when (status) {
        DownloadStatus.QUEUED ->
            CircularProgressIndicator(
                modifier = indicatorModifier,
                strokeWidth = 2.dp,
            )
        DownloadStatus.DOWNLOADING ->
            CircularProgressIndicator(
                progress = { progressPercent.coerceIn(0, 100) / 100f },
                modifier = indicatorModifier,
                strokeWidth = 2.dp,
            )
        DownloadStatus.COMPLETED ->
            Icon(imageVector = Icons.Filled.DownloadDone, contentDescription = null)
        DownloadStatus.FAILED ->
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
        DownloadStatus.NONE ->
            Icon(imageVector = Icons.Filled.Download, contentDescription = null)
    }
}
