package com.anhtuta.lilimusic.core.playback

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SkipPreviousPolicyTest {
    @Test
    fun repeatOneReplaysTheSameTrack() {
        assertThat(SkipPreviousPolicy.decide(repeatOne = true, currentIndex = 0))
            .isEqualTo(SkipPreviousAction.ReplayCurrent)
        assertThat(SkipPreviousPolicy.decide(repeatOne = true, currentIndex = 2))
            .isEqualTo(SkipPreviousAction.ReplayCurrent)
    }

    @Test
    fun firstTrackStaysPut() {
        assertThat(SkipPreviousPolicy.decide(repeatOne = false, currentIndex = 0))
            .isEqualTo(SkipPreviousAction.StayAtStart)
    }

    @Test
    fun laterTracksGoToPrevious() {
        assertThat(SkipPreviousPolicy.decide(repeatOne = false, currentIndex = 1))
            .isEqualTo(SkipPreviousAction.GoToPrevious)
    }
}
