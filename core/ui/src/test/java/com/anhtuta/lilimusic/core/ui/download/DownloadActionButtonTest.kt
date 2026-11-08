package com.anhtuta.lilimusic.core.ui.download

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DownloadActionButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun idleShowsDownloadContentDescriptionAndFiresClick() {
        var clicks = 0
        setButton(DownloadStatus.NONE) { clicks++ }

        composeRule.onNodeWithContentDescription("Download").assertIsDisplayed()
        composeRule.onAllNodesWithText("Download").assertCountEquals(0)
        composeRule.onNodeWithContentDescription("Download").performClick()
        assertThat(clicks).isEqualTo(1)
    }

    @Test
    fun queuedUsesCancel() {
        setButton(DownloadStatus.QUEUED)
        composeRule.onNodeWithContentDescription("Cancel").assertIsDisplayed()
        composeRule.onAllNodesWithContentDescription("Download").assertCountEquals(0)
    }

    @Test
    fun downloadingUsesCancel() {
        setButton(DownloadStatus.DOWNLOADING, progressPercent = 40)
        composeRule.onNodeWithContentDescription("Cancel").assertIsDisplayed()
    }

    @Test
    fun completedUsesDelete() {
        setButton(DownloadStatus.COMPLETED)
        composeRule.onNodeWithContentDescription("Delete").assertIsDisplayed()
        composeRule.onAllNodesWithContentDescription("Download").assertCountEquals(0)
    }

    @Test
    fun failedUsesRetry() {
        setButton(DownloadStatus.FAILED)
        composeRule.onNodeWithContentDescription("Retry").assertIsDisplayed()
    }

    private fun setButton(
        status: DownloadStatus,
        progressPercent: Int = 0,
        onClick: () -> Unit = {},
    ) {
        composeRule.setContent {
            LiliTheme {
                DownloadActionButton(
                    status = status,
                    progressPercent = progressPercent,
                    onClick = onClick,
                )
            }
        }
    }
}
