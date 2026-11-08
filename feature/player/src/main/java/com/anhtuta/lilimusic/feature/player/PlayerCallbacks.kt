package com.anhtuta.lilimusic.feature.player

data class PlayerCallbacks(
    val onBack: () -> Unit,
    val onPlayPause: () -> Unit,
    val onSkipNext: () -> Unit,
    val onSkipPrevious: () -> Unit,
    val onSeek: (Long) -> Unit,
    val onSeekBack: () -> Unit = {},
    val onSeekForward: () -> Unit = {},
    val onToggleShuffle: () -> Unit = {},
    val onToggleRepeatOne: () -> Unit = {},
    val onCyclePlayMode: () -> Unit = {},
    val onIncreaseSpeed: () -> Unit = {},
    val onDecreaseSpeed: () -> Unit = {},
    val onSetSpeed: (Float) -> Unit = {},
    val onSettings: () -> Unit = {},
    val onDownload: () -> Unit = {},
    val onLyricCard: () -> Unit = {},
)
