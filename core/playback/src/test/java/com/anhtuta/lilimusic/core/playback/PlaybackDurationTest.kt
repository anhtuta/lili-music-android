package com.anhtuta.lilimusic.core.playback

import androidx.media3.common.C
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaybackDurationTest {
    @Test
    fun unsetDurationFallsBackToTenMinutes() {
        assertThat(PlaybackDuration.fromMedia3(C.TIME_UNSET)).isEqualTo(600_000L)
    }

    @Test
    fun knownDurationIsUnchanged() {
        assertThat(PlaybackDuration.fromMedia3(123_456L)).isEqualTo(123_456L)
    }
}
