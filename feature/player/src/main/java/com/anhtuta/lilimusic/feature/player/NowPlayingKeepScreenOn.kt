package com.anhtuta.lilimusic.feature.player

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun rememberNowPlayingKeepScreenOn(
    songPath: String?,
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
    var keepOn by remember { mutableStateOf(false) }
    LaunchedEffect(songPath, eligible) {
        if (!eligible) {
            keepOn = false
            return@LaunchedEffect
        }
        val sessionStartMs = SystemClock.elapsedRealtime()
        keepOn = true
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
