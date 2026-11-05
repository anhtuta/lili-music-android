package com.anhtuta.lilimusic.feature.player

data class PlayerCallbacks(
    val onBack: () -> Unit,
    val onPlayPause: () -> Unit,
    val onSkipNext: () -> Unit,
    val onSkipPrevious: () -> Unit,
    val onSeek: (Long) -> Unit,
    val onToggleShuffle: () -> Unit,
    val onToggleRepeatOne: () -> Unit,
    val onIncreaseSpeed: () -> Unit,
    val onDecreaseSpeed: () -> Unit,
)
