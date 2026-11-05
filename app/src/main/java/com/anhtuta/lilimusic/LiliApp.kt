package com.anhtuta.lilimusic

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.anhtuta.lilimusic.feature.library.LibraryRoute
import com.anhtuta.lilimusic.feature.lyrics.LyricsRoute
import com.anhtuta.lilimusic.feature.player.MiniPlayerBar
import com.anhtuta.lilimusic.feature.player.PlayerRoute
import com.anhtuta.lilimusic.feature.player.PlayerViewModel
import com.anhtuta.lilimusic.feature.settings.SettingsRoute
import com.anhtuta.lilimusic.feature.settings.SettingsViewModel
import com.anhtuta.lilimusic.navigation.LiliNavHost
import com.anhtuta.lilimusic.navigation.PlayerDestination
import com.anhtuta.lilimusic.navigation.SettingsDestination

@Composable
fun LiliApp(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val playback by playerViewModel.uiState.collectAsStateWithLifecycle()
    val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val onPlayerScreen = navBackStackEntry?.destination?.hasRoute<PlayerDestination>() == true
    val onSettingsScreen = navBackStackEntry?.destination?.hasRoute<SettingsDestination>() == true

    NotificationPermissionEffect(isPlaying = playback.isPlaying)

    LiliTheme(darkTheme = settings.settings.appTheme == AppTheme.DARK) {
        Column(modifier = Modifier.fillMaxSize()) {
            LiliNavHost(
                navController = navController,
                modifier = Modifier.weight(1f),
                libraryContent = { onSongClick, onSettings ->
                    LibraryRoute(
                        onSongClick = { song -> onSongClick(song.path) },
                        onSettingsClick = onSettings,
                    )
                },
                playerContent = { path, onBack, onSettings ->
                    PlayerRoute(
                        path = path,
                        onBack = onBack,
                        viewModel = playerViewModel,
                        lyrics = { LyricsRoute() },
                        onSettingsClick = onSettings,
                    )
                },
                settingsContent = { onBack ->
                    SettingsRoute(onBack = onBack)
                },
            )
            val currentSong = playback.song
            if (!onPlayerScreen && !onSettingsScreen && currentSong != null) {
                MiniPlayerBar(
                    song = currentSong,
                    isPlaying = playback.isPlaying,
                    onPlayPause = playerViewModel::playPause,
                    onClick = { navController.navigate(PlayerDestination(currentSong.path)) },
                )
            }
        }
    }
}

@Composable
private fun NotificationPermissionEffect(isPlaying: Boolean) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(isPlaying) {
        if (isPlaying && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
