package com.anhtuta.lilimusic.feature.lyrics

import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont

data class LyricCardCallbacks(
    val onClose: () -> Unit,
    val onToggleLine: (Int) -> Unit,
    val onSelectAll: () -> Unit,
    val onGenerate: () -> Unit,
    val onBackToSelection: () -> Unit,
    val onShare: () -> Unit,
    val onCopy: () -> Unit,
    val onTextAlign: (LyricAlign) -> Unit = {},
    val onTextColor: (String) -> Unit = {},
    val onBold: (Boolean) -> Unit = {},
    val onBackgroundColor: (String) -> Unit = {},
    val onUseSongImage: (Boolean) -> Unit = {},
    val onOverlayOpacity: (Int) -> Unit = {},
    val onBrightnessBoost: (Int) -> Unit = {},
    val onFontFamily: (LyricCardFont) -> Unit = {},
    val onFontSize: (Int) -> Unit = {},
)
