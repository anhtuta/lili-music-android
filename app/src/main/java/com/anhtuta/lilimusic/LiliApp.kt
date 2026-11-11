package com.anhtuta.lilimusic

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.anhtuta.lilimusic.core.ui.theme.SystemBarAppearance
import com.anhtuta.lilimusic.feature.downloads.DownloadsRoute
import com.anhtuta.lilimusic.feature.downloads.DownloadsViewModel
import com.anhtuta.lilimusic.feature.library.LibraryRoute
import com.anhtuta.lilimusic.feature.lyrics.LyricCardRoute
import com.anhtuta.lilimusic.feature.lyrics.LyricCardViewModel
import com.anhtuta.lilimusic.feature.player.MiniPlayerBar
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
    downloadsViewModel: DownloadsViewModel = hiltViewModel(),
    lyricCardViewModel: LyricCardViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    DeepLinkNewIntentEffect(navController)
    val playback by playerViewModel.uiState.collectAsStateWithLifecycle()
    val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val downloads by downloadsViewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val onPlayerScreen = navBackStackEntry?.destination?.hasRoute<PlayerDestination>() == true
    val onSettingsScreen = navBackStackEntry?.destination?.hasRoute<SettingsDestination>() == true

    NotificationPermissionEffect(
        request = playback.isPlaying || downloads.items.any { it.isActive },
    )

    val darkTheme = settings.settings.appTheme == AppTheme.DARK
    ApplySystemBarAppearance(darkTheme = darkTheme)
    LiliTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    LiliNavHost(
                        navController = navController,
                        modifier = Modifier.weight(1f),
                        libraryContent = { onSongClick, onSettings, onDownloads ->
                            LibraryRoute(
                                onSongClick = { song -> onSongClick(song.path) },
                                onSettingsClick = onSettings,
                                onDownloadsClick = onDownloads,
                            )
                        },
                        playerContent = { path, onBack, onSettings ->
                            NowPlayingEntry(
                                path = path,
                                onBack = onBack,
                                onSettingsClick = onSettings,
                                onLyricCardClick = lyricCardViewModel::open,
                                playerViewModel = playerViewModel,
                            )
                        },
                        settingsContent = { onBack ->
                            SettingsRoute(onBack = onBack)
                        },
                        downloadsContent = { onBack, onPlay ->
                            DownloadsRoute(
                                onBack = onBack,
                                onPlay = { item -> onPlay(item.songPath) },
                                viewModel = downloadsViewModel,
                            )
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
                LyricCardRoute(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    viewModel = lyricCardViewModel,
                )
            }
        }
    }
}

@Composable
private fun ApplySystemBarAppearance(darkTheme: Boolean) {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        val lightIcons = SystemBarAppearance.isAppearanceLightStatusBars(darkTheme)
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = lightIcons
            isAppearanceLightNavigationBars = lightIcons
        }
    }
}

@Composable
private fun DeepLinkNewIntentEffect(navController: NavHostController) {
    val context = LocalContext.current
    DisposableEffect(navController, context) {
        val activity = context as? ComponentActivity
        if (activity == null) {
            return@DisposableEffect onDispose { }
        }
        val listener =
            Consumer<Intent> { incoming ->
                navController.handleDeepLink(incoming)
            }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }
}

@Composable
private fun NotificationPermissionEffect(request: Boolean) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(request) {
        if (request && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
