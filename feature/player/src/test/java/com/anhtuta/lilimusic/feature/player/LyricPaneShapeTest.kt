package com.anhtuta.lilimusic.feature.player

import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricPaneShapeTest {
    @Test
    fun lyricPaneUsesSixteenDpCorners() {
        assertThat(LyricPaneShape.CornerRadius).isEqualTo(16.dp)
    }
}
