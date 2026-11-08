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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.ui.R
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton

const val DownloadActionGlyphTag = "download-action-glyph"

@Composable
fun DownloadActionButton(
    status: DownloadStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    progressPercent: Int = 0,
    iconSize: Dp = 24.dp,
    containerSize: Dp? = null,
) {
    val description = stringResource(descriptionRes(status))
    LiliIconButton(
        label = description,
        onClick = onClick,
        modifier = modifier,
        containerSize = containerSize,
    ) {
        DownloadActionVisual(
            status = status,
            progressPercent = progressPercent,
            iconSize = iconSize,
        )
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
    iconSize: Dp,
) {
    val glyphModifier =
        Modifier
            .size(iconSize)
            .testTag(DownloadActionGlyphTag)
    when (status) {
        DownloadStatus.QUEUED ->
            CircularProgressIndicator(
                modifier = glyphModifier,
                strokeWidth = 2.dp,
            )
        DownloadStatus.DOWNLOADING ->
            CircularProgressIndicator(
                progress = { progressPercent.coerceIn(0, 100) / 100f },
                modifier = glyphModifier,
                strokeWidth = 2.dp,
            )
        DownloadStatus.COMPLETED ->
            Icon(
                imageVector = Icons.Filled.DownloadDone,
                contentDescription = null,
                modifier = glyphModifier,
            )
        DownloadStatus.FAILED ->
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = glyphModifier,
            )
        DownloadStatus.NONE ->
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = null,
                modifier = glyphModifier,
            )
    }
}
