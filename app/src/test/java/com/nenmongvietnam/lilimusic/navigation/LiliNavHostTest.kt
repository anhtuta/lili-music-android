package com.nenmongvietnam.lilimusic.navigation

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
                libraryContent = {
                    Text("Library screen", modifier = Modifier.testTag("library-destination"))
                },
                playerContent = { _, _ -> Text("Player screen") },
            )
        }

        composeRule.onNodeWithTag("library-destination").assertIsDisplayed()
        composeRule.runOnIdle {
            assertThat(navController.currentDestination?.hasRoute<LibraryDestination>()).isTrue()
        }
    }

    @Test
    fun navigatingToPlayerShowsPlaceholderRoute() {
        lateinit var navController: NavHostController
        composeRule.setContent {
            navController = rememberNavController()
            LiliNavHost(
                navController = navController,
                libraryContent = {
                    Text("Library screen")
                },
                playerContent = { songId, _ ->
                    Text("Player for $songId", modifier = Modifier.testTag("player-destination"))
                },
            )
            LaunchedEffect(Unit) {
                navController.navigate(PlayerDestination(songId = 99L))
            }
        }

        composeRule.onNodeWithTag("player-destination").assertIsDisplayed()
        composeRule.onNodeWithText("Player for 99").assertIsDisplayed()
        composeRule.runOnIdle {
            assertThat(navController.currentDestination?.hasRoute<PlayerDestination>()).isTrue()
        }
    }
}
