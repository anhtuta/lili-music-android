package com.anhtuta.lilimusic.feature.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.ui.R as CoreUiR
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton

internal object NowPlayingOverflowMetrics {
    val SettingsIconSize: Dp = 16.dp
    val SettingsContainerSize: Dp = 36.dp
    val MenuIconSize: Dp = 20.dp
}

@Composable
internal fun NowPlayingOverflowMenu(
    song: Song?,
    downloadStatus: DownloadStatus,
    downloadProgress: Int,
    callbacks: PlayerCallbacks,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        LiliIconButton(
            icon = Icons.Filled.Settings,
            label = stringResource(R.string.player_more_options),
            onClick = { expanded = true },
            iconSize = NowPlayingOverflowMetrics.SettingsIconSize,
            containerSize = NowPlayingOverflowMetrics.SettingsContainerSize,
            modifier = Modifier.testTag(PlayerTestTags.SETTINGS),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.testTag(PlayerTestTags.OVERFLOW_MENU),
        ) {
            if (song != null) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(downloadMenuLabelRes(downloadStatus))) },
                    onClick = {
                        expanded = false
                        callbacks.onDownload()
                    },
                    leadingIcon = {
                        DownloadMenuGlyph(
                            status = downloadStatus,
                            progressPercent = downloadProgress,
                            iconSize = NowPlayingOverflowMetrics.MenuIconSize,
                        )
                    },
                    modifier = Modifier.testTag(PlayerTestTags.DOWNLOAD),
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.player_lyric_card)) },
                    onClick = {
                        expanded = false
                        callbacks.onLyricCard()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = null,
                            modifier = Modifier.size(NowPlayingOverflowMetrics.MenuIconSize),
                        )
                    },
                    modifier = Modifier.testTag(PlayerTestTags.LYRIC_CARD),
                )
            }
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.player_settings)) },
                onClick = {
                    expanded = false
                    callbacks.onSettings()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(NowPlayingOverflowMetrics.MenuIconSize),
                    )
                },
                modifier = Modifier.testTag(PlayerTestTags.SETTINGS_SCREEN),
            )
        }
    }
}

@Composable
private fun DownloadMenuGlyph(
    status: DownloadStatus,
    progressPercent: Int,
    iconSize: Dp,
) {
    val modifier = Modifier.size(iconSize)
    when (status) {
        DownloadStatus.QUEUED ->
            CircularProgressIndicator(modifier = modifier, strokeWidth = 2.dp)
        DownloadStatus.DOWNLOADING ->
            CircularProgressIndicator(
                progress = { progressPercent.coerceIn(0, 100) / 100f },
                modifier = modifier,
                strokeWidth = 2.dp,
            )
        DownloadStatus.COMPLETED ->
            Icon(imageVector = Icons.Filled.DownloadDone, contentDescription = null, modifier = modifier)
        DownloadStatus.FAILED ->
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = modifier)
        DownloadStatus.NONE ->
            Icon(imageVector = Icons.Filled.Download, contentDescription = null, modifier = modifier)
    }
}

private fun downloadMenuLabelRes(status: DownloadStatus): Int =
    when (status) {
        DownloadStatus.NONE -> CoreUiR.string.download_action_download
        DownloadStatus.QUEUED, DownloadStatus.DOWNLOADING -> CoreUiR.string.download_action_cancel
        DownloadStatus.COMPLETED -> CoreUiR.string.download_action_delete
        DownloadStatus.FAILED -> CoreUiR.string.download_action_retry
    }
