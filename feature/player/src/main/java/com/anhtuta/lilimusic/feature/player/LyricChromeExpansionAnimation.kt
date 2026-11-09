package com.anhtuta.lilimusic.feature.player

import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp

internal object LyricChromeExpansionAnimation {
    val ControlsSlideDistance = 72.dp
    val ClickTweenMs = 280

    fun clickSpec() = tween<Float>(durationMillis = ClickTweenMs)
}
