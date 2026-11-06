package com.anhtuta.lilimusic.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LiliNavHostTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun startDestinationIsLibrary() {
        lateinit var navController: NavHostController
        composeRule.setContent {
            navController = rememberNavController()
            LiliNavHost(
                navController = navController,
                libraryContent = { _, _, _ ->
                    Text("Library screen", modifier = Modifier.testTag("library-destination"))
                },
                playerContent = { _, _, _ -> Text("Player screen") },
                settingsContent = { Text("Settings screen") },
                downloadsContent = { _, _ -> Text("Downloads screen") },
            )
        }

        composeRule.onNodeWithTag("library-destination").assertIsDisplayed()
        composeRule.runOnIdle {
            assertThat(navController.currentDestination?.hasRoute<LibraryDestination>()).isTrue()
        }
    }

    @Test
    fun navigatingToPlayerUsesSongPathSlug() {
        lateinit var navController: NavHostController
        composeRule.setContent {
            navController = rememberNavController()
            LiliNavHost(
                navController = navController,
                libraryContent = { _, _, _ ->
                    Text("Library screen")
                },
                playerContent = { path, _, _ ->
                    Text("Player for $path", modifier = Modifier.testTag("player-destination"))
                },
                settingsContent = { Text("Settings screen") },
                downloadsContent = { _, _ -> Text("Downloads screen") },
            )
            LaunchedEffect(Unit) {
                navController.navigate(PlayerDestination(path = "hello-artist"))
            }
        }

        composeRule.onNodeWithTag("player-destination").assertIsDisplayed()
        composeRule.onNodeWithText("Player for hello-artist").assertIsDisplayed()
        composeRule.runOnIdle {
            assertThat(navController.currentDestination?.hasRoute<PlayerDestination>()).isTrue()
        }
    }

    @Test
    fun navigatingToSettingsShowsSettingsContent() {
        lateinit var navController: NavHostController
        composeRule.setContent {
            navController = rememberNavController()
            LiliNavHost(
                navController = navController,
                libraryContent = { _, _, _ -> Text("Library screen") },
                playerContent = { _, _, _ -> Text("Player screen") },
                settingsContent = {
                    Text("Settings screen", modifier = Modifier.testTag("settings-destination"))
                },
                downloadsContent = { _, _ -> Text("Downloads screen") },
            )
            LaunchedEffect(Unit) {
                navController.navigate(SettingsDestination)
            }
        }

        composeRule.onNodeWithTag("settings-destination").assertIsDisplayed()
        composeRule.runOnIdle {
            assertThat(navController.currentDestination?.hasRoute<SettingsDestination>()).isTrue()
        }
    }

    @Test
    fun navigatingToDownloadsShowsDownloadsContent() {
        lateinit var navController: NavHostController
        composeRule.setContent {
            navController = rememberNavController()
            LiliNavHost(
                navController = navController,
                libraryContent = { _, _, _ -> Text("Library screen") },
                playerContent = { _, _, _ -> Text("Player screen") },
                settingsContent = { Text("Settings screen") },
                downloadsContent = { _, _ ->
                    Text("Downloads screen", modifier = Modifier.testTag("downloads-destination"))
                },
            )
            LaunchedEffect(Unit) {
                navController.navigate(DownloadsDestination)
            }
        }

        composeRule.onNodeWithTag("downloads-destination").assertIsDisplayed()
        composeRule.runOnIdle {
            assertThat(navController.currentDestination?.hasRoute<DownloadsDestination>()).isTrue()
        }
    }
}
