package com.anhtuta.lilimusic.core.ui.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
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
    fun compactGlyphIsTwentyDpAndTouchTargetAtLeastForty() {
        composeRule.setContent {
            LiliTheme {
                LiliIconButton(
                    icon = Icons.Filled.Settings,
                    label = "Settings",
                    onClick = {},
                    iconSize = 20.dp,
                    containerSize = 40.dp,
                    modifier = Modifier.testTag("compact"),
                )
            }
        }

        val glyph =
            composeRule
                .onNodeWithTag(LiliIconButtonTags.GLYPH, useUnmergedTree = true)
                .getUnclippedBoundsInRoot()
        assertThat(glyph.bottom.value - glyph.top.value).isWithin(1.5f).of(20f)
        val button = composeRule.onNodeWithTag("compact").getUnclippedBoundsInRoot()
        assertThat(button.bottom.value - button.top.value).isAtLeast(40f)
        composeRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Settings").performTouchInput { longClick() }
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
    }
}
