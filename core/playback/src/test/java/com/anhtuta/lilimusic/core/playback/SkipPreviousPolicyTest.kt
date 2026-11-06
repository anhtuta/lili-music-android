package com.anhtuta.lilimusic.core.playback

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SkipPreviousPolicyTest {
    @Test
    fun repeatOneReplaysTheSameTrack() {
        assertThat(SkipPreviousPolicy.decide(repeatOne = true))
            .isEqualTo(SkipPreviousAction.ReplayCurrent)
    }

    @Test
    fun sequenceAndShuffleAlwaysGoToPreviousTrack() {
        assertThat(SkipPreviousPolicy.decide(repeatOne = false))
            .isEqualTo(SkipPreviousAction.GoToPrevious)
    }
}
