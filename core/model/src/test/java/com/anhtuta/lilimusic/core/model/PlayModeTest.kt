package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlayModeTest {
    @Test
    fun unknownStorageValueDefaultsToSequence() {
        assertThat(PlayMode.fromStorage(null)).isEqualTo(PlayMode.SEQUENCE)
        assertThat(PlayMode.fromStorage("")).isEqualTo(PlayMode.SEQUENCE)
        assertThat(PlayMode.fromStorage("random")).isEqualTo(PlayMode.SEQUENCE)
    }

    @Test
    fun parsesWebPlayTypeValues() {
        assertThat(PlayMode.fromStorage("SEQUENCE")).isEqualTo(PlayMode.SEQUENCE)
        assertThat(PlayMode.fromStorage("SHUFFLE")).isEqualTo(PlayMode.SHUFFLE)
        assertThat(PlayMode.fromStorage("REPEAT_ONE")).isEqualTo(PlayMode.REPEAT_ONE)
    }

    @Test
    fun cycleFollowsWebPlayTypeListOrder() {
        assertThat(PlayMode.SEQUENCE.cycle()).isEqualTo(PlayMode.SHUFFLE)
        assertThat(PlayMode.SHUFFLE.cycle()).isEqualTo(PlayMode.REPEAT_ONE)
        assertThat(PlayMode.REPEAT_ONE.cycle()).isEqualTo(PlayMode.SEQUENCE)
    }

    @Test
    fun shuffleAndRepeatOneEachToggleBackToSequence() {
        assertThat(PlayMode.SEQUENCE.toggleShuffle()).isEqualTo(PlayMode.SHUFFLE)
        assertThat(PlayMode.SHUFFLE.toggleShuffle()).isEqualTo(PlayMode.SEQUENCE)
        assertThat(PlayMode.REPEAT_ONE.toggleShuffle()).isEqualTo(PlayMode.SHUFFLE)

        assertThat(PlayMode.SEQUENCE.toggleRepeatOne()).isEqualTo(PlayMode.REPEAT_ONE)
        assertThat(PlayMode.REPEAT_ONE.toggleRepeatOne()).isEqualTo(PlayMode.SEQUENCE)
        assertThat(PlayMode.SHUFFLE.toggleRepeatOne()).isEqualTo(PlayMode.REPEAT_ONE)
    }
}
