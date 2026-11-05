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
    libraryContent: @Composable (onSongClick: (String) -> Unit) -> Unit,
    playerContent: @Composable (path: String, onBack: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = LibraryDestination,
        modifier = modifier,
    ) {
        composable<LibraryDestination> {
            libraryContent { path ->
                navController.navigate(PlayerDestination(path))
            }
        }
        composable<PlayerDestination>(
            deepLinks =
                listOf(
                    navDeepLink { uriPattern = "lilimusic://bai-hat/{path}" },
                ),
        ) { entry ->
            val destination = entry.toRoute<PlayerDestination>()
            playerContent(destination.path) {
                navController.popBackStack()
            }
        }
    }
}
