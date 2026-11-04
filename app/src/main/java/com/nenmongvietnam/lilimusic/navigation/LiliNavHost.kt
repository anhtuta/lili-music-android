package com.nenmongvietnam.lilimusic.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun LiliNavHost(
    libraryContent: @Composable (onSongClick: (Long) -> Unit) -> Unit,
    playerContent: @Composable (songId: Long, onBack: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = LibraryDestination,
        modifier = modifier,
    ) {
        composable<LibraryDestination> {
            libraryContent { songId ->
                navController.navigate(PlayerDestination(songId))
            }
        }
        composable<PlayerDestination> { entry ->
            val destination = entry.toRoute<PlayerDestination>()
            playerContent(destination.songId) {
                navController.popBackStack()
            }
        }
    }
}
