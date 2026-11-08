package com.anhtuta.lilimusic.core.ui.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LiliIconButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun clickFiresAndLabelIsContentDescriptionNotVisibleText() {
        var clicks = 0
        setButton { clicks++ }

        composeRule.onNodeWithContentDescription("Previous song").assertIsDisplayed()
        composeRule.onAllNodesWithText("Previous song").assertCountEquals(0)
        composeRule.onNodeWithContentDescription("Previous song").performClick()
        assertThat(clicks).isEqualTo(1)
    }

    @Test
    fun longPressShowsTooltipWithTheSameLabel() {
        setButton()

        composeRule.onNodeWithContentDescription("Previous song").performTouchInput { longClick() }
        composeRule.onNodeWithText("Previous song").assertIsDisplayed()
    }

    @Test
    fun darkThemeIconButtonRemainsFindable() {
        composeRule.setContent {
            LiliTheme(darkTheme = true) {
                LiliIconButton(
                    icon = Icons.Filled.PlayArrow,
                    label = "Settings",
                    onClick = {},
                )
            }
        }
        composeRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
    }

    @Test
    fun filledPlayButtonExposesContentDescription() {
        var clicks = 0
        composeRule.setContent {
            LiliTheme(darkTheme = true) {
                LiliFilledIconButton(
                    icon = Icons.Filled.PlayArrow,
                    label = "Play",
                    onClick = { clicks++ },
                )
            }
        }
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed().performClick()
        assertThat(clicks).isEqualTo(1)
    }

    private fun setButton(onClick: () -> Unit = {}) {
        composeRule.setContent {
            LiliTheme {
                LiliIconButton(
                    icon = Icons.Filled.PlayArrow,
                    label = "Previous song",
                    onClick = onClick,
                )
            }
        }
    }
}
