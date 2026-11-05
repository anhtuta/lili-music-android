package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaybackSpeedTest {
    @Test
    fun stepsByOneTenthAndClamps() {
        assertThat(PlaybackSpeed.increase(1.0f)).isEqualTo(1.1f)
        assertThat(PlaybackSpeed.decrease(1.0f)).isEqualTo(0.9f)
        assertThat(PlaybackSpeed.increase(PlaybackSpeed.MAX)).isEqualTo(PlaybackSpeed.MAX)
        assertThat(PlaybackSpeed.decrease(PlaybackSpeed.MIN)).isEqualTo(PlaybackSpeed.MIN)
    }

    @Test
    fun coerceRoundsToStep() {
        assertThat(PlaybackSpeed.coerce(1.14f)).isEqualTo(1.1f)
        assertThat(PlaybackSpeed.coerce(0.4f)).isEqualTo(PlaybackSpeed.MIN)
        assertThat(PlaybackSpeed.coerce(2.4f)).isEqualTo(PlaybackSpeed.MAX)
    }
}
