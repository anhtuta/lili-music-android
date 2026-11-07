package com.anhtuta.lilimusic.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute

@Composable
fun LiliNavHost(
    libraryContent: @Composable (onSongClick: (String) -> Unit, onSettings: () -> Unit, onDownloads: () -> Unit) -> Unit,
    playerContent: @Composable (path: String, onBack: () -> Unit, onSettings: () -> Unit) -> Unit,
    settingsContent: @Composable (onBack: () -> Unit) -> Unit,
    downloadsContent: @Composable (onBack: () -> Unit, onPlay: (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = LibraryDestination,
        modifier = modifier,
    ) {
        composable<LibraryDestination> {
            libraryContent(
                { path -> navController.navigate(PlayerDestination(path)) },
                { navController.navigate(SettingsDestination) },
                { navController.navigate(DownloadsDestination) },
            )
        }
        composable<PlayerDestination>(
            deepLinks =
                listOf(
                    navDeepLink { uriPattern = "lilimusic://bai-hat/{path}" },
                    navDeepLink { uriPattern = "https://blog.nenmongvietnam.com/bai-hat/{path}" },
                    navDeepLink {
                        uriPattern = "https://anhtuta.github.io/lili-player/bai-hat/{path}"
                    },
                ),
        ) { entry ->
            val destination = entry.toRoute<PlayerDestination>()
            playerContent(
                destination.path,
                { navController.popBackStack() },
                { navController.navigate(SettingsDestination) },
            )
        }
        composable<SettingsDestination> {
            settingsContent { navController.popBackStack() }
        }
        composable<DownloadsDestination> {
            downloadsContent(
                { navController.popBackStack() },
                { path -> navController.navigate(PlayerDestination(path)) },
            )
        }
    }
}
