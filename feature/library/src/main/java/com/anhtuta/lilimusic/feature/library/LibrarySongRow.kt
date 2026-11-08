package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.ui.download.DownloadActionButton

@Composable
fun LibrarySongRow(
    song: Song,
    soyYears: List<Int>,
    onClick: () -> Unit,
    onCoverClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverUrl: String? = song.imageUrl,
    unavailableOffline: Boolean = false,
    downloadStatus: DownloadStatus = DownloadStatus.NONE,
    downloadProgress: Int = 0,
    onDownloadAction: (() -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .testTag(LibraryTestTags.song(song.id))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SongCover(song = song, coverUrl = coverUrl, onCoverClick = onCoverClick)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = song.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = song.artist, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            SongMetaRow(song = song, soyYears = soyYears)
            if (unavailableOffline) {
                Text(
                    text = stringResource(R.string.library_unavailable_offline),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag(LibraryTestTags.unavailable(song.id)),
                )
            }
        }
        if (onDownloadAction != null) {
            DownloadActionButton(
                status = downloadStatus,
                progressPercent = downloadProgress,
                onClick = onDownloadAction,
                modifier = Modifier.testTag(LibraryTestTags.download(song.id)),
            )
        }
    }
}

@Composable
fun CoverPreviewDialog(
    song: Song,
    onDismiss: () -> Unit,
    coverUrl: String? = song.imageUrl,
) {
    val imageUrl = coverUrl ?: return
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = onDismiss)
                    .padding(16.dp)
                    .testTag(LibraryTestTags.COVER_PREVIEW),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.library_cover_preview_cd, song.title),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .widthIn(max = 400.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = stringResource(R.string.library_cover_preview_caption, song.artist, song.title),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun SongCover(
    song: Song,
    coverUrl: String?,
    onCoverClick: () -> Unit,
) {
    if (coverUrl == null) {
        val placeholderDescription = stringResource(R.string.library_cover_placeholder_cd, song.title)
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .testTag(LibraryTestTags.cover(song.id))
                    .semantics { contentDescription = placeholderDescription },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = song.title.take(1),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    } else {
        AsyncImage(
            model = coverUrl,
            contentDescription = stringResource(R.string.library_cover_cd, song.title),
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .testTag(LibraryTestTags.cover(song.id))
                    .clickable(onClick = onCoverClick),
            contentScale = ContentScale.Crop,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongMetaRow(
    song: Song,
    soyYears: List<Int>,
) {
    val listensLabel = stringResource(R.string.library_listens)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text(text = listensLabel) } },
                state = rememberTooltipState(),
            ) {
                Icon(
                    imageVector = Icons.Filled.Headphones,
                    contentDescription = listensLabel,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = song.listens.toString(),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.testTag(LibraryTestTags.listens(song.id)),
            )
        }
        song.songOfTheYear?.let { year ->
            val soyDescription = stringResource(R.string.library_soy_cd, year)
            Text(
                text = year.toString(),
                color = Color(SongOfTheYearPalette.contentArgb(year, soyYears)),
                style = MaterialTheme.typography.labelMedium,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(SongOfTheYearPalette.backgroundArgb(year, soyYears)))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag(LibraryTestTags.soy(song.id))
                        .semantics { contentDescription = soyDescription },
            )
        }
    }
}
