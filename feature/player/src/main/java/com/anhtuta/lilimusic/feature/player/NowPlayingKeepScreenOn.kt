package com.anhtuta.lilimusic.feature.player

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Up to [NowPlayingKeepScreenOnPolicy.MAX_ELIGIBLE_DURATION_MS] of keep-on per **Now Playing visit**
 * (leaving the route clears the cap). Skipping to another song does not reset the timer.
 */
@Composable
fun rememberNowPlayingKeepScreenOn(
    settingEnabled: Boolean,
    songHasLyricFile: Boolean,
    syncedLyricsActive: Boolean,
    isPlaying: Boolean,
): Boolean {
    val eligible =
        NowPlayingKeepScreenOnPolicy.isEligible(
            settingEnabled = settingEnabled,
            songHasLyricFile = songHasLyricFile,
            syncedLyricsActive = syncedLyricsActive,
            isPlaying = isPlaying,
        )
    var visitCapStartMs by remember { mutableLongStateOf(-1L) }
    var keepOn by remember { mutableStateOf(false) }
    LaunchedEffect(eligible) {
        if (!eligible) {
            keepOn = false
            return@LaunchedEffect
        }
        if (visitCapStartMs < 0L) {
            visitCapStartMs = SystemClock.elapsedRealtime()
        }
        val sessionStartMs = visitCapStartMs
        keepOn = NowPlayingKeepScreenOnPolicy.withinCap(SystemClock.elapsedRealtime() - sessionStartMs)
        while (isActive) {
            delay(NowPlayingKeepScreenOnPolicy.CAP_TICK_MS)
            val elapsed = SystemClock.elapsedRealtime() - sessionStartMs
            val withinCap = NowPlayingKeepScreenOnPolicy.withinCap(elapsed)
            keepOn = withinCap
            if (!withinCap) break
        }
    }
    return keepOn
}
