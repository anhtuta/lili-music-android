package com.anhtuta.lilimusic.feature.player

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NowPlayingKeepScreenOnPolicyTest {
    @Test
    fun eligibleRequiresAllConditions() {
        assertThat(
            NowPlayingKeepScreenOnPolicy.isEligible(
                settingEnabled = true,
                songHasLyricFile = true,
                syncedLyricsActive = true,
                isPlaying = true,
            ),
        ).isTrue()
        assertThat(
            NowPlayingKeepScreenOnPolicy.isEligible(
                settingEnabled = false,
                songHasLyricFile = true,
                syncedLyricsActive = true,
                isPlaying = true,
            ),
        ).isFalse()
        assertThat(
            NowPlayingKeepScreenOnPolicy.isEligible(
                settingEnabled = true,
                songHasLyricFile = false,
                syncedLyricsActive = true,
                isPlaying = true,
            ),
        ).isFalse()
        assertThat(
            NowPlayingKeepScreenOnPolicy.isEligible(
                settingEnabled = true,
                songHasLyricFile = true,
                syncedLyricsActive = false,
                isPlaying = true,
            ),
        ).isFalse()
        assertThat(
            NowPlayingKeepScreenOnPolicy.isEligible(
                settingEnabled = true,
                songHasLyricFile = true,
                syncedLyricsActive = true,
                isPlaying = false,
            ),
        ).isFalse()
    }

    @Test
    fun capIsTenMinutes() {
        assertThat(NowPlayingKeepScreenOnPolicy.MAX_ELIGIBLE_DURATION_MS).isEqualTo(600_000L)
        assertThat(NowPlayingKeepScreenOnPolicy.withinCap(599_999L)).isTrue()
        assertThat(NowPlayingKeepScreenOnPolicy.withinCap(600_000L)).isFalse()
    }
}
