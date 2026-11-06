package com.anhtuta.lilimusic.feature.downloads

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DownloadsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyStateIsShown() {
        composeRule.setContent {
            LiliTheme {
                DownloadsScreen(state = DownloadsUiState(), callbacks = noopCallbacks())
            }
        }

        composeRule.onNodeWithTag(DownloadsTestTags.EMPTY).assertIsDisplayed()
    }

    @Test
    fun retryDeleteAndPlayFireCallbacks() {
        var retried = 0
        var deleted = 0
        var played = 0
        composeRule.setContent {
            LiliTheme {
                DownloadsScreen(
                    state =
                        DownloadsUiState(
                            items =
                                listOf(
                                    testDownload(path = "fail", title = "Fail", status = DownloadStatus.FAILED),
                                    testDownload(path = "ok", title = "Ok", status = DownloadStatus.COMPLETED),
                                ),
                        ),
                    callbacks =
                        noopCallbacks().copy(
                            onRetry = { retried++ },
                            onDelete = { deleted++ },
                            onPlay = { played++ },
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(DownloadsTestTags.retry("fail")).performClick()
        composeRule.onNodeWithTag(DownloadsTestTags.delete("fail")).performClick()
        composeRule.onNodeWithTag(DownloadsTestTags.play("ok")).performClick()
        assertThat(retried).isEqualTo(1)
        assertThat(deleted).isEqualTo(1)
        assertThat(played).isEqualTo(1)
    }

    @Test
    fun downloadingShowsProgress() {
        composeRule.setContent {
            LiliTheme {
                DownloadsScreen(
                    state =
                        DownloadsUiState(
                            items = listOf(testDownload(path = "busy", status = DownloadStatus.DOWNLOADING, progressPercent = 40)),
                        ),
                    callbacks = noopCallbacks(),
                )
            }
        }

        composeRule.onNodeWithTag(DownloadsTestTags.progress("busy")).assertIsDisplayed()
    }

    @Test
    fun deleteAllConfirmFiresCallback() {
        var confirmed = 0
        composeRule.setContent {
            LiliTheme {
                DownloadsScreen(
                    state =
                        DownloadsUiState(
                            items = listOf(testDownload()),
                            confirmDeleteAll = true,
                        ),
                    callbacks = noopCallbacks().copy(onConfirmDeleteAll = { confirmed++ }),
                )
            }
        }

        composeRule.onNodeWithTag(DownloadsTestTags.CONFIRM_DELETE_ALL).performClick()
        assertThat(confirmed).isEqualTo(1)
    }

    private fun noopCallbacks(): DownloadsCallbacks =
        DownloadsCallbacks(
            onBack = {},
            onPlay = {},
            onRetry = {},
            onDelete = {},
            onDeleteAllClick = {},
            onConfirmDeleteAll = {},
            onCancelDeleteAll = {},
        )
}
