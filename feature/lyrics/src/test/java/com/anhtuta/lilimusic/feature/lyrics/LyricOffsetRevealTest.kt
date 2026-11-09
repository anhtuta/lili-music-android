package com.anhtuta.lilimusic.feature.lyrics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricOffsetRevealTest {
    @Test
    fun listDragRevealTracksUserDraggingOnly() {
        assertThat(LyricOffsetReveal.shouldRevealForListDrag(userDragging = true)).isTrue()
        assertThat(LyricOffsetReveal.shouldRevealForListDrag(userDragging = false)).isFalse()
    }
}
