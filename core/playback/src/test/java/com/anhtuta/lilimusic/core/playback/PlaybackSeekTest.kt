package com.anhtuta.lilimusic.core.playback

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaybackSeekTest {
    @Test
    fun incrementMatchesWebFiveSeconds() {
        assertThat(PlaybackSeek.INCREMENT_MS).isEqualTo(5_000L)
    }
}
