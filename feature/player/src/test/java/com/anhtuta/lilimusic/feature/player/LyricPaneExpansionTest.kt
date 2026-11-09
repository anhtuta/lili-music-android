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

    @Test
    fun dragProgressTracksPullBetweenCollapsedAndExpanded() {
        assertThat(LyricPaneExpansion.dragProgress(expanded = false, dragY = 0f)).isEqualTo(0f)
        assertThat(LyricPaneExpansion.dragProgress(expanded = false, dragY = 24f)).isWithin(0.001f).of(0.5f)
        assertThat(LyricPaneExpansion.dragProgress(expanded = false, dragY = 80f)).isEqualTo(1f)
        assertThat(LyricPaneExpansion.dragProgress(expanded = true, dragY = -24f)).isWithin(0.001f).of(0.5f)
        assertThat(LyricPaneExpansion.dragProgress(expanded = true, dragY = 0f)).isEqualTo(1f)
    }
}
