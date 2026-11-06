package com.anhtuta.lilimusic.feature.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
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
import com.anhtuta.lilimusic.core.model.PlayMode
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
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PlayerCover(title = song.title, imageUrl = song.imageUrl)
        Spacer(modifier = Modifier.height(24.dp))
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
        Spacer(modifier = Modifier.height(16.dp))
        PlayerSeekBar(
            positionMs = state.positionMs,
            durationMs = state.durationMs,
            onSeek = callbacks.onSeek,
        )
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
        Spacer(modifier = Modifier.height(8.dp))
        PlayerTransport(
            isPlaying = state.isPlaying,
            callbacks = callbacks,
        )
        Spacer(modifier = Modifier.height(16.dp))
        PlayerModeRow(
            playMode = state.playMode,
            speed = state.speed,
            callbacks = callbacks,
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(top = 16.dp),
        ) {
            lyrics()
        }
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
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = callbacks.onSkipPrevious,
            modifier = Modifier.testTag(PlayerTestTags.PREVIOUS),
        ) {
            Text(text = stringResource(R.string.player_previous))
        }
        val rewindDescription = stringResource(R.string.player_rewind_cd)
        IconButton(
            onClick = callbacks.onSeekBack,
            modifier =
                Modifier
                    .testTag(PlayerTestTags.REWIND)
                    .semantics { contentDescription = rewindDescription },
        ) {
            Text(text = stringResource(R.string.player_rewind))
        }
        TextButton(
            onClick = callbacks.onPlayPause,
            modifier = Modifier.testTag(PlayerTestTags.PLAY_PAUSE),
        ) {
            Text(
                text =
                    stringResource(
                        if (isPlaying) R.string.player_pause else R.string.player_play,
                    ),
            )
        }
        val forwardDescription = stringResource(R.string.player_forward_cd)
        IconButton(
            onClick = callbacks.onSeekForward,
            modifier =
                Modifier
                    .testTag(PlayerTestTags.FORWARD)
                    .semantics { contentDescription = forwardDescription },
        ) {
            Text(text = stringResource(R.string.player_forward))
        }
        IconButton(
            onClick = callbacks.onSkipNext,
            modifier = Modifier.testTag(PlayerTestTags.NEXT),
        ) {
            Text(text = stringResource(R.string.player_next))
        }
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
