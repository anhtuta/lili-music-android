package com.anhtuta.lilimusic.feature.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

@Composable
fun KeepScreenOnEffect(active: Boolean) {
    val view = LocalView.current
    DisposableEffect(active) {
        val previous = view.keepScreenOn
        view.keepScreenOn = active
        onDispose {
            view.keepScreenOn = previous
        }
    }
}
