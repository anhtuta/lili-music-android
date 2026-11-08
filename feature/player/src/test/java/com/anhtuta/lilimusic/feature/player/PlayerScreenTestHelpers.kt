package com.anhtuta.lilimusic.feature.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.ui.palette.CoverPaletteColors

internal val PlayerScreenCompactWidth = 400.dp
internal val PlayerScreenExpandedWidth = 800.dp
internal val PlayerScreenPhoneHeight = 720.dp

@Composable
internal fun SizedPlayer(
    width: Dp,
    height: Dp,
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    lyrics: @Composable () -> Unit = {},
    coverPaletteOverride: CoverPaletteColors? = null,
) {
    Box(modifier = Modifier.size(width = width, height = height)) {
        PlayerScreen(
            modifier = Modifier.fillMaxSize(),
            state = state,
            callbacks = callbacks,
            lyrics = lyrics,
            coverPaletteOverride = coverPaletteOverride,
        )
    }
}

internal fun idlePlayerCallbacks() =
    PlayerCallbacks(
        onBack = {},
        onPlayPause = {},
        onSkipNext = {},
        onSkipPrevious = {},
        onSeek = {},
        onToggleShuffle = {},
        onToggleRepeatOne = {},
        onIncreaseSpeed = {},
        onDecreaseSpeed = {},
    )
