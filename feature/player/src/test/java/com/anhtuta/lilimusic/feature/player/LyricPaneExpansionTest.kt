package com.anhtuta.lilimusic.feature.player

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricPaneExpansionTest {
    @Test
    fun dragDownExpandsAndDragUpCollapses() {
        assertThat(LyricPaneExpansion.afterVerticalDrag(expanded = false, dragY = 80f)).isTrue()
        assertThat(LyricPaneExpansion.afterVerticalDrag(expanded = true, dragY = -80f)).isFalse()
        assertThat(LyricPaneExpansion.afterVerticalDrag(expanded = false, dragY = 10f)).isFalse()
        assertThat(LyricPaneExpansion.afterVerticalDrag(expanded = true, dragY = -10f)).isTrue()
    }
}
