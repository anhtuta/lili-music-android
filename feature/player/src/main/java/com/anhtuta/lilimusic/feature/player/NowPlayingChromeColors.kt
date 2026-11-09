package com.anhtuta.lilimusic.feature.player

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.anhtuta.lilimusic.core.ui.palette.CoverPalette

internal object NowPlayingChromeColors {
    fun controlsOnSurface(
        darkTheme: Boolean,
        themeOnSurface: Color,
    ): Color = if (darkTheme) Color.White else themeOnSurface

    @Composable
    fun controlsOnSurface(): Color =
        controlsOnSurface(
            darkTheme = !CoverPalette.isLight(MaterialTheme.colorScheme.background),
            themeOnSurface = MaterialTheme.colorScheme.onSurface,
        )
}
