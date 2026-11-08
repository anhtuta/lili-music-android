package com.anhtuta.lilimusic.feature.player

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NowPlayingDismissTest {
    @Test
    fun distanceOrFlingDismisses() {
        assertThat(NowPlayingDismiss.shouldDismiss(draggedDownPx = 140f)).isTrue()
        assertThat(NowPlayingDismiss.shouldDismiss(draggedDownPx = 20f)).isFalse()
        assertThat(NowPlayingDismiss.shouldDismiss(draggedDownPx = 20f, flingY = 3000f)).isTrue()
        assertThat(NowPlayingDismiss.shouldDismiss(draggedDownPx = 20f, flingY = -3000f)).isFalse()
    }
}
