package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LyricCardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun selectionTogglesLineAndGenerates() {
        var toggled = -1
        var generate = 0
        composeRule.setContent {
            LiliTheme {
                LyricCardScreen(
                    state =
                        LyricCardUiState(
                            step = LyricCardStep.SELECT,
                            lines =
                                listOf(
                                    LyricCardSelectableLine(0, "First line", selected = true),
                                    LyricCardSelectableLine(1, "Second line", selected = false),
                                ),
                            selectedText = "First line",
                        ),
                    callbacks =
                        LyricCardCallbacks(
                            onClose = {},
                            onToggleLine = { toggled = it },
                            onSelectAll = {},
                            onGenerate = { generate++ },
                            onBackToSelection = {},
                            onShare = {},
                            onCopy = {},
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(LyricCardTestTags.SELECTION).assertIsDisplayed()
        composeRule.onNodeWithTag(LyricCardTestTags.line(0)).assertIsDisplayed()
        composeRule.onNodeWithTag(LyricCardTestTags.SELECTED_PREVIEW).assertIsDisplayed()
        composeRule.onNodeWithTag(LyricCardTestTags.line(1)).performClick()
        assertThat(toggled).isEqualTo(1)
        composeRule.onNodeWithTag(LyricCardTestTags.GENERATE).performClick()
        assertThat(generate).isEqualTo(1)
    }

    @Test
    fun configureFiresShareCopyAndStyleCallbacks() {
        var share = 0
        var copy = 0
        var align: LyricAlign? = null
        var font: LyricCardFont? = null
        var useImage: Boolean? = null
        composeRule.setContent {
            LiliTheme {
                LyricCardScreen(
                    state =
                        LyricCardUiState(
                            step = LyricCardStep.CONFIGURE,
                            selectedText = "First line",
                            songTitle = "Alpha",
                            songArtist = "Artist",
                        ),
                    callbacks =
                        LyricCardCallbacks(
                            onClose = {},
                            onToggleLine = {},
                            onSelectAll = {},
                            onGenerate = {},
                            onBackToSelection = {},
                            onShare = { share++ },
                            onCopy = { copy++ },
                            onTextAlign = { align = it },
                            onFontFamily = { font = it },
                            onUseSongImage = { useImage = it },
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(LyricCardTestTags.CONFIGURE).assertIsDisplayed()
        composeRule.onNodeWithTag(LyricCardTestTags.PREVIEW).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag(LyricCardTestTags.align("center")).performScrollTo().performClick()
        assertThat(align).isEqualTo(LyricAlign.CENTER)
        composeRule.onNodeWithTag(LyricCardTestTags.BG_COLOR).performScrollTo().performClick()
        assertThat(useImage).isFalse()
        composeRule.onNodeWithTag(LyricCardTestTags.font("Georgia")).performScrollTo().performClick()
        assertThat(font).isEqualTo(LyricCardFont.GEORGIA)
        composeRule.onNodeWithTag(LyricCardTestTags.SHARE).performScrollTo().performClick()
        composeRule.onNodeWithTag(LyricCardTestTags.COPY).performScrollTo().performClick()
        assertThat(share).isEqualTo(1)
        assertThat(copy).isEqualTo(1)
    }

    @Test
    fun darkThemeCloseIconStaysFindable() {
        composeRule.setContent {
            LiliTheme(darkTheme = true) {
                LyricCardScreen(
                    state =
                        LyricCardUiState(
                            step = LyricCardStep.SELECT,
                            lines = listOf(LyricCardSelectableLine(0, "First line", selected = false)),
                        ),
                    callbacks =
                        LyricCardCallbacks(
                            onClose = {},
                            onToggleLine = {},
                            onSelectAll = {},
                            onGenerate = {},
                            onBackToSelection = {},
                            onShare = {},
                            onCopy = {},
                        ),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Close").assertIsDisplayed()
    }
}
