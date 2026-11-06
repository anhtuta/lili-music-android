package com.anhtuta.lilimusic.core.playback

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaybackTickerTest {
    @Test
    fun lyricPositionTickerMatchesWebThirtyMs() {
        assertThat(Media3PlayerController.POSITION_TICK_MS).isEqualTo(30L)
    }
}
