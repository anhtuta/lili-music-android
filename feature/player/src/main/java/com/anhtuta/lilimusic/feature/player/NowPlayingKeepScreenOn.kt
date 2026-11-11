package com.anhtuta.lilimusic.feature.player

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

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
    var eligibleStartMs by remember { mutableLongStateOf(-1L) }
    LaunchedEffect(eligible) {
        if (eligible) {
            if (eligibleStartMs < 0L) {
                eligibleStartMs = SystemClock.elapsedRealtime()
            }
        } else {
            eligibleStartMs = -1L
        }
    }
    val tick by produceState(initialValue = 0, key1 = eligible) {
        if (!eligible) return@produceState
        while (true) {
            delay(1_000)
            value += 1
        }
    }
    if (!eligible || eligibleStartMs < 0L) return false
    val elapsed = SystemClock.elapsedRealtime() - eligibleStartMs
    return NowPlayingKeepScreenOnPolicy.withinCap(elapsed).also { _ -> tick }
}
