package com.anhtuta.lilimusic.feature.player

import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlayerLayoutTest {
    @Test
    fun compactMatchesWeb720Breakpoint() {
        assertThat(PlayerLayout.isCompact(719.dp)).isTrue()
        assertThat(PlayerLayout.isCompact(400.dp)).isTrue()
        assertThat(PlayerLayout.isCompact(720.dp)).isFalse()
        assertThat(PlayerLayout.isCompact(800.dp)).isFalse()
    }
}
