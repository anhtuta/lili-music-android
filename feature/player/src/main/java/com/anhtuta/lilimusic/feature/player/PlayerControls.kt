package com.anhtuta.lilimusic.feature.player

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.ui.button.LiliFilledIconButton
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton
import java.util.Locale

@Composable
internal fun PlayerControlsBar(
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
            playMode = state.playMode,
            speed = state.speed,
            callbacks = callbacks,
        )
    }
}

@Composable
internal fun PlayerSongHeadings(song: Song) {
    Text(
        text = song.title,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag(PlayerTestTags.TITLE),
        maxLines = 1,
    )
    Text(
        text = song.artist,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.testTag(PlayerTestTags.ARTIST),
        maxLines = 1,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlayerSeekBar(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
) {
    var sliderValue by remember(positionMs, durationMs) {
        mutableFloatStateOf(if (durationMs > 0L) positionMs.toFloat() / durationMs.toFloat() else 0f)
    }
    val colors =
        SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f),
        )
    Slider(
        value = sliderValue.coerceIn(0f, 1f),
        onValueChange = { sliderValue = it },
        onValueChangeFinished = {
            onSeek((sliderValue * durationMs).toLong().coerceAtLeast(0L))
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .height(20.dp)
                .testTag(PlayerTestTags.SEEK),
        colors = colors,
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(4.dp),
                colors = colors,
            )
        },
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = remember { MutableInteractionSource() },
                modifier = Modifier.size(12.dp),
                colors = colors,
            )
        },
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
internal fun PlayerTransport(
    isPlaying: Boolean,
    playMode: PlayMode,
    speed: Float,
    callbacks: PlayerCallbacks,
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val accent = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        PlayerSpeedChip(
            speed = speed,
            onSetSpeed = callbacks.onSetSpeed,
        )
        LiliIconButton(
            icon = Icons.Filled.SkipPrevious,
            label = stringResource(R.string.player_previous_cd),
            onClick = callbacks.onSkipPrevious,
            modifier = Modifier.testTag(PlayerTestTags.PREVIOUS),
            tint = onSurface,
        )
        LiliIconButton(
            icon = Icons.Filled.Replay5,
            label = stringResource(R.string.player_rewind_cd),
            onClick = callbacks.onSeekBack,
            modifier = Modifier.testTag(PlayerTestTags.REWIND),
            tint = onSurface,
        )
        LiliFilledIconButton(
            icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            label =
                stringResource(
                    if (isPlaying) R.string.player_pause else R.string.player_play,
                ),
            onClick = callbacks.onPlayPause,
            modifier = Modifier.testTag(PlayerTestTags.PLAY_PAUSE),
        )
        LiliIconButton(
            icon = Icons.Filled.Forward5,
            label = stringResource(R.string.player_forward_cd),
            onClick = callbacks.onSeekForward,
            modifier = Modifier.testTag(PlayerTestTags.FORWARD),
            tint = onSurface,
        )
        LiliIconButton(
            icon = Icons.Filled.SkipNext,
            label = stringResource(R.string.player_next_cd),
            onClick = callbacks.onSkipNext,
            modifier = Modifier.testTag(PlayerTestTags.NEXT),
            tint = onSurface,
        )
        PlayModeButton(
            playMode = playMode,
            onClick = callbacks.onCyclePlayMode,
            tint = if (playMode == PlayMode.SEQUENCE) onSurface else accent,
        )
    }
}

@Composable
internal fun PlayModeButton(
    playMode: PlayMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    val modeName =
        stringResource(
            when (playMode) {
                PlayMode.SEQUENCE -> R.string.player_play_mode_sequence
                PlayMode.SHUFFLE -> R.string.player_shuffle
                PlayMode.REPEAT_ONE -> R.string.player_repeat_one
            },
        )
    LiliIconButton(
        icon =
            when (playMode) {
                PlayMode.SEQUENCE -> Icons.Filled.Repeat
                PlayMode.SHUFFLE -> Icons.Filled.Shuffle
                PlayMode.REPEAT_ONE -> Icons.Filled.RepeatOne
            },
        label = stringResource(R.string.player_play_mode, modeName),
        onClick = onClick,
        modifier = modifier.testTag(PlayerTestTags.PLAY_MODE),
        tint = tint,
    )
}

@Composable
internal fun PlayerSpeedChip(
    speed: Float,
    onSetSpeed: (Float) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val label =
        stringResource(
            R.string.player_speed_value,
            String.format(Locale.US, "%.1f", speed),
        )
    Box {
        FilterChip(
            selected = false,
            onClick = { expanded = true },
            label = {
                Text(
                    text = label,
                    modifier = Modifier.testTag(PlayerTestTags.SPEED_VALUE),
                )
            },
            modifier =
                Modifier
                    .widthIn(min = 52.dp)
                    .testTag(PlayerTestTags.SPEED_CHIP),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.testTag(PlayerTestTags.SPEED_MENU),
        ) {
            PlaybackSpeed.steps().forEach { step ->
                val stepLabel =
                    stringResource(
                        R.string.player_speed_value,
                        String.format(Locale.US, "%.1f", step),
                    )
                DropdownMenuItem(
                    text = { Text(text = stepLabel) },
                    onClick = {
                        onSetSpeed(step)
                        expanded = false
                    },
                )
            }
        }
    }
}
