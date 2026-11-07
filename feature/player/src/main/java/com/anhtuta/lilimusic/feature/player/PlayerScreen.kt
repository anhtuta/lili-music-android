package com.anhtuta.lilimusic.feature.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.ui.palette.CoverPaletteColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    modifier: Modifier = Modifier,
    lyrics: @Composable () -> Unit = {},
    coverPaletteOverride: CoverPaletteColors? = null,
) {
    val loadedPalette = rememberCoverPalette(state.song?.imageUrl)
    val palette = coverPaletteOverride ?: loadedPalette
    Scaffold(
        modifier = modifier.testTag(PlayerTestTags.SCREEN),
        containerColor = palette?.background ?: MaterialTheme.colorScheme.background,
        contentColor = palette?.onBackground ?: MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.player_title)) },
                navigationIcon = {
                    TextButton(
                        onClick = callbacks.onBack,
                        modifier = Modifier.testTag(PlayerTestTags.BACK),
                    ) {
                        Text(stringResource(R.string.player_back))
                    }
                },
                actions = {
                    if (state.song != null) {
                        TextButton(
                            onClick = callbacks.onDownload,
                            modifier = Modifier.testTag(PlayerTestTags.DOWNLOAD),
                        ) {
                            Text(text = downloadActionLabel(state))
                        }
                        TextButton(
                            onClick = callbacks.onLyricCard,
                            modifier = Modifier.testTag(PlayerTestTags.LYRIC_CARD),
                        ) {
                            Text(stringResource(R.string.player_lyric_card))
                        }
                    }
                    TextButton(
                        onClick = callbacks.onSettings,
                        modifier = Modifier.testTag(PlayerTestTags.SETTINGS),
                    ) {
                        Text(stringResource(R.string.player_settings))
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            if (palette != null) {
                Box(modifier = Modifier.testTag(PlayerTestTags.PALETTE))
            }
            PlayerBody(
                state = state,
                callbacks = callbacks,
                lyrics = lyrics,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun PlayerBody(
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    lyrics: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val song = state.song
    if (song == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.player_empty))
        }
        return
    }
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (PlayerLayout.isCompact(maxWidth)) {
            CompactPlayerBody(
                song = song,
                state = state,
                callbacks = callbacks,
                lyrics = lyrics,
            )
        } else {
            ExpandedPlayerBody(
                song = song,
                state = state,
                callbacks = callbacks,
                lyrics = lyrics,
            )
        }
    }
}

@Composable
private fun CompactPlayerBody(
    song: Song,
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    lyrics: @Composable () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag(PlayerTestTags.LYRICS),
        ) {
            lyrics()
        }
        PlayerControlsBar(
            song = song,
            state = state,
            callbacks = callbacks,
            showHeadings = true,
        )
    }
}

@Composable
private fun ExpandedPlayerBody(
    song: Song,
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    lyrics: @Composable () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                PlayerCover(title = song.title, imageUrl = song.imageUrl)
                Spacer(modifier = Modifier.height(16.dp))
                PlayerSongHeadings(song = song)
            }
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag(PlayerTestTags.LYRICS),
            ) {
                lyrics()
            }
        }
        PlayerControlsBar(
            song = song,
            state = state,
            callbacks = callbacks,
            showHeadings = false,
        )
    }
}

@Composable
private fun PlayerControlsBar(
    song: Song,
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    showHeadings: Boolean,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag(PlayerTestTags.CONTROLS),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showHeadings) {
            PlayerSongHeadings(song = song)
            Spacer(modifier = Modifier.height(8.dp))
        }
        PlayerSeekBar(
            positionMs = state.positionMs,
            durationMs = state.durationMs,
            onSeek = callbacks.onSeek,
        )
        PlayerQueueMessages(state = state)
        Spacer(modifier = Modifier.height(8.dp))
        PlayerTransport(
            isPlaying = state.isPlaying,
            callbacks = callbacks,
        )
        Spacer(modifier = Modifier.height(8.dp))
        PlayerModeRow(
            playMode = state.playMode,
            speed = state.speed,
            callbacks = callbacks,
        )
    }
}

@Composable
private fun PlayerSongHeadings(song: Song) {
    Text(
        text = song.title,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag(PlayerTestTags.TITLE),
    )
    Text(
        text = song.artist,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.testTag(PlayerTestTags.ARTIST),
    )
    val album = song.album?.takeIf { it.isNotBlank() } ?: stringResource(R.string.player_no_album)
    Text(
        text = stringResource(R.string.player_album, album),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag(PlayerTestTags.ALBUM),
    )
}

@Composable
private fun PlayerQueueMessages(state: PlayerUiState) {
    if (state.reachedEnd) {
        Text(
            text = stringResource(R.string.player_end_of_queue),
            color = MaterialTheme.colorScheme.error,
            modifier =
                Modifier
                    .padding(top = 8.dp)
                    .testTag(PlayerTestTags.END_OF_QUEUE),
        )
    }
    if (state.atStartOfQueue) {
        Text(
            text = stringResource(R.string.player_first_of_queue),
            color = MaterialTheme.colorScheme.error,
            modifier =
                Modifier
                    .padding(top = 8.dp)
                    .testTag(PlayerTestTags.FIRST_OF_QUEUE),
        )
    }
}

@Composable
private fun PlayerCover(
    title: String,
    imageUrl: String?,
) {
    if (imageUrl == null) {
        val placeholderDescription = stringResource(R.string.player_cover_placeholder_cd, title)
        Box(
            modifier =
                Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag(PlayerTestTags.COVER_PLACEHOLDER)
                    .semantics { contentDescription = placeholderDescription },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = title.take(1), style = MaterialTheme.typography.displayLarge)
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.player_cover_cd, title),
            modifier =
                Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag(PlayerTestTags.COVER),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun PlayerSeekBar(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
) {
    var sliderValue by remember(positionMs, durationMs) {
        mutableFloatStateOf(if (durationMs > 0L) positionMs.toFloat() / durationMs.toFloat() else 0f)
    }
    Slider(
        value = sliderValue.coerceIn(0f, 1f),
        onValueChange = { sliderValue = it },
        onValueChangeFinished = {
            onSeek((sliderValue * durationMs).toLong().coerceAtLeast(0L))
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag(PlayerTestTags.SEEK),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = PlayerTimeFormatter.format(positionMs),
            modifier = Modifier.testTag(PlayerTestTags.POSITION),
        )
        Text(
            text = PlayerTimeFormatter.format(durationMs),
            modifier = Modifier.testTag(PlayerTestTags.DURATION),
        )
    }
}

@Composable
private fun PlayerTransport(
    isPlaying: Boolean,
    callbacks: PlayerCallbacks,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val rewindDescription = stringResource(R.string.player_rewind_cd)
        val forwardDescription = stringResource(R.string.player_forward_cd)
        TransportTextButton(
            onClick = callbacks.onSkipPrevious,
            label = stringResource(R.string.player_previous),
            modifier = Modifier.testTag(PlayerTestTags.PREVIOUS),
        )
        TransportTextButton(
            onClick = callbacks.onSeekBack,
            label = stringResource(R.string.player_rewind),
            modifier =
                Modifier
                    .testTag(PlayerTestTags.REWIND)
                    .semantics { contentDescription = rewindDescription },
        )
        TransportTextButton(
            onClick = callbacks.onPlayPause,
            label =
                stringResource(
                    if (isPlaying) R.string.player_pause else R.string.player_play,
                ),
            modifier = Modifier.testTag(PlayerTestTags.PLAY_PAUSE),
        )
        TransportTextButton(
            onClick = callbacks.onSeekForward,
            label = stringResource(R.string.player_forward),
            modifier =
                Modifier
                    .testTag(PlayerTestTags.FORWARD)
                    .semantics { contentDescription = forwardDescription },
        )
        TransportTextButton(
            onClick = callbacks.onSkipNext,
            label = stringResource(R.string.player_next),
            modifier = Modifier.testTag(PlayerTestTags.NEXT),
        )
    }
}

@Composable
private fun RowScope.TransportTextButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.weight(1f),
    ) {
        Text(
            text = label,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PlayerModeRow(
    playMode: PlayMode,
    speed: Float,
    callbacks: PlayerCallbacks,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterChip(
            selected = playMode == PlayMode.SHUFFLE,
            onClick = callbacks.onToggleShuffle,
            label = { Text(stringResource(R.string.player_shuffle)) },
            modifier = Modifier.testTag(PlayerTestTags.SHUFFLE),
        )
        FilterChip(
            selected = playMode == PlayMode.REPEAT_ONE,
            onClick = callbacks.onToggleRepeatOne,
            label = { Text(stringResource(R.string.player_repeat_one)) },
            modifier = Modifier.testTag(PlayerTestTags.REPEAT_ONE),
        )
    }
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(
            onClick = callbacks.onDecreaseSpeed,
            modifier = Modifier.testTag(PlayerTestTags.SPEED_DOWN),
        ) {
            Text(stringResource(R.string.player_speed_down))
        }
        Text(
            text =
                stringResource(
                    R.string.player_speed_value,
                    String.format(Locale.US, "%.1f", speed),
                ),
            modifier = Modifier.testTag(PlayerTestTags.SPEED_VALUE),
        )
        TextButton(
            onClick = callbacks.onIncreaseSpeed,
            modifier = Modifier.testTag(PlayerTestTags.SPEED_UP),
        ) {
            Text(stringResource(R.string.player_speed_up))
        }
    }
}

@Composable
private fun downloadActionLabel(state: PlayerUiState): String =
    when (state.downloadStatus) {
        DownloadStatus.NONE -> stringResource(R.string.player_download)
        DownloadStatus.QUEUED -> stringResource(R.string.player_download_waiting)
        DownloadStatus.DOWNLOADING -> stringResource(R.string.player_download_progress, state.downloadProgress)
        DownloadStatus.COMPLETED -> stringResource(R.string.player_download_delete)
        DownloadStatus.FAILED -> stringResource(R.string.player_download_retry)
    }
