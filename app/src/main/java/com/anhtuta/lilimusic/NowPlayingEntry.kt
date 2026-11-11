package com.anhtuta.lilimusic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhtuta.lilimusic.feature.lyrics.LyricsRoute
import com.anhtuta.lilimusic.feature.lyrics.LyricsViewModel
import com.anhtuta.lilimusic.feature.player.PlayerRoute
import com.anhtuta.lilimusic.feature.player.PlayerViewModel
import com.anhtuta.lilimusic.feature.player.rememberNowPlayingKeepScreenOn

@Composable
fun NowPlayingEntry(
    path: String,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onLyricCardClick: () -> Unit,
    playerViewModel: PlayerViewModel = hiltViewModel(),
    lyricsViewModel: LyricsViewModel = hiltViewModel(),
) {
    val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()
    val lyricsState by lyricsViewModel.uiState.collectAsStateWithLifecycle()
    val keepScreenOn =
        rememberNowPlayingKeepScreenOn(
            settingEnabled = lyricsState.settings.keepScreenOnWhileLyrics,
            songHasLyricFile = playerState.song?.lyric != null,
            syncedLyricsActive = lyricsState.frame != null,
            isPlaying = playerState.isPlaying,
        )
    PlayerRoute(
        path = path,
        onBack = onBack,
        viewModel = playerViewModel,
        keepScreenOn = keepScreenOn,
        lyrics = { LyricsRoute(viewModel = lyricsViewModel) },
        onSettingsClick = onSettingsClick,
        onLyricCardClick = onLyricCardClick,
    )
}
