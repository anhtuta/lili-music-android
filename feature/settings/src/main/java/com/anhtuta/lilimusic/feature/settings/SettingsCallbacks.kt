package com.anhtuta.lilimusic.feature.settings

import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme

data class SettingsCallbacks(
    val onBack: () -> Unit,
    val onLyricAlign: (LyricAlign) -> Unit,
    val onLyricTheme: (LyricTheme) -> Unit,
    val onKaraokeMode: (Boolean) -> Unit,
    val onBlurMode: (Boolean) -> Unit,
    val onWordAppear: (Boolean) -> Unit,
    val onLineAppear: (Boolean) -> Unit,
    val onGiganticLine: (Boolean) -> Unit,
    val onTranslate: (Boolean) -> Unit,
    val onAppTheme: (AppTheme) -> Unit,
    val onIncreaseSpeed: () -> Unit,
    val onDecreaseSpeed: () -> Unit,
)
