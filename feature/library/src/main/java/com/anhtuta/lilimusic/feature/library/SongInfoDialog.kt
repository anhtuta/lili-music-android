package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.Song

@Composable
internal fun SongInfoDialog(
    song: Song,
    onDismiss: () -> Unit,
) {
    val none = stringResource(R.string.library_info_none)
    val rows =
        SongInfoFields.rows(
            song = song,
            labels =
                SongInfoLabels(
                    title = stringResource(R.string.library_info_title),
                    artist = stringResource(R.string.library_info_artist),
                    album = stringResource(R.string.library_info_album),
                    type = stringResource(R.string.library_info_type),
                    songOfTheYear = stringResource(R.string.library_info_soy),
                    lyricType = stringResource(R.string.library_info_lyric_type),
                    lyricFile = stringResource(R.string.library_info_lyric_file),
                    listens = stringResource(R.string.library_listens),
                    date = stringResource(R.string.library_info_date),
                    createdDate = stringResource(R.string.library_info_created_date),
                    fileName = stringResource(R.string.library_info_file_name),
                    path = stringResource(R.string.library_info_path),
                    id = stringResource(R.string.library_info_id),
                    zingId = stringResource(R.string.library_info_zing_id),
                    imageName = stringResource(R.string.library_info_image_name),
                    imageUrl = stringResource(R.string.library_info_image_url),
                    streamUrl = stringResource(R.string.library_info_stream_url),
                ),
            none = none,
        )
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag(LibraryTestTags.SONG_INFO),
        title = { Text(text = stringResource(R.string.library_song_info)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = row.label,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = row.value,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.library_info_close))
            }
        },
    )
}
