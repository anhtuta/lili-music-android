package com.anhtuta.lilimusic.feature.player

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlayerChromeLayoutTest {
    @Test
    fun controlsUseDocumentedInsets() {
        assertThat(PlayerControlsInsets.Horizontal.value).isEqualTo(16f)
        assertThat(PlayerControlsInsets.ExtraBottom.value).isEqualTo(12f)
    }

    @Test
    fun overflowMenuUsesSmallerGear() {
        assertThat(NowPlayingOverflowMetrics.SettingsIconSize.value).isEqualTo(16f)
        assertThat(NowPlayingOverflowMetrics.SettingsContainerSize.value).isEqualTo(36f)
    }
}
