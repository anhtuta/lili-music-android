package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton

@Composable
internal fun LibraryScrollableHeader(
    onRefresh: () -> Unit,
    onDownloadsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = LibraryChromeInsets.Horizontal,
                    end = LibraryChromeInsets.TrailingAction,
                    top = 8.dp,
                    bottom = 4.dp,
                ).testTag(LibraryTestTags.HEADER),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.library_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            LiliIconButton(
                icon = Icons.Filled.Refresh,
                label = stringResource(R.string.library_refresh),
                onClick = onRefresh,
                modifier = Modifier.testTag(LibraryTestTags.REFRESH),
            )
            LiliIconButton(
                icon = Icons.Filled.DownloadForOffline,
                label = stringResource(R.string.library_downloads),
                onClick = onDownloadsClick,
                modifier = Modifier.testTag(LibraryTestTags.DOWNLOADS),
            )
            LiliIconButton(
                icon = Icons.Filled.Settings,
                label = stringResource(R.string.library_settings),
                onClick = onSettingsClick,
                modifier = Modifier.testTag(LibraryTestTags.SETTINGS),
            )
        }
    }
}
