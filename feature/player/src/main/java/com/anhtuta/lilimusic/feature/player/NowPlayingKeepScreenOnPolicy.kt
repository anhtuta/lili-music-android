package com.anhtuta.lilimusic.feature.player

object NowPlayingKeepScreenOnPolicy {
    const val MAX_ELIGIBLE_DURATION_MS = 10 * 60 * 1000L

    fun isEligible(
        settingEnabled: Boolean,
        songHasLyricFile: Boolean,
        syncedLyricsActive: Boolean,
        isPlaying: Boolean,
    ): Boolean = settingEnabled && songHasLyricFile && syncedLyricsActive && isPlaying

    fun withinCap(elapsedEligibleMs: Long): Boolean = elapsedEligibleMs < MAX_ELIGIBLE_DURATION_MS
}
