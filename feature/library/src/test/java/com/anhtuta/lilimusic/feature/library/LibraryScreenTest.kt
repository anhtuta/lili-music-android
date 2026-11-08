package com.anhtuta.lilimusic.feature.library

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LibraryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun listShowsTitlesAndClickDelegates() {
        var clickedId: Long? = null
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state =
                        LibraryUiState(
                            songs =
                                listOf(
                                    libraryTestSong(id = 1, title = "Alpha", artist = "Ann"),
                                    libraryTestSong(id = 2, title = "Beta", artist = "Bob"),
                                ),
                            isLoading = false,
                        ),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = { clickedId = it.id },
                )
            }
        }

        composeRule.onNodeWithText("Alpha").assertIsDisplayed()
        composeRule.onNodeWithText("Bob").assertIsDisplayed()
        composeRule.onNodeWithTag(LibraryTestTags.listens(1), useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag(LibraryTestTags.type(1), useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag(LibraryTestTags.date(1), useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag(LibraryTestTags.song(1)).performClick()
        assertThat(clickedId).isEqualTo(1L)
    }

    @Test
    fun soyBadgeAndCoverPreviewOpenWhenImageExists() {
        var clickedId: Long? = null
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state =
                        LibraryUiState(
                            songs =
                                listOf(
                                    libraryTestSong(
                                        id = 1,
                                        title = "Alpha",
                                        artist = "Ann",
                                        songOfTheYear = 2024,
                                        imageUrl = "https://example.com/alpha.jpg",
                                    ),
                                ),
                            isLoading = false,
                        ),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = { clickedId = it.id },
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.soy(1), useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("2024").assertIsDisplayed()
        composeRule.onNodeWithTag(LibraryTestTags.cover(1), useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag(LibraryTestTags.COVER_PREVIEW).assertIsDisplayed()
        composeRule.onNodeWithText("Ann - Alpha").assertIsDisplayed()
        assertThat(clickedId).isNull()
    }

    @Test
    fun coverWithoutImageDoesNotOpenPreview() {
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state =
                        LibraryUiState(
                            songs = listOf(libraryTestSong(id = 1, title = "Alpha", imageUrl = null)),
                            isLoading = false,
                        ),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.cover(1), useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag(LibraryTestTags.COVER_PREVIEW).assertDoesNotExist()
    }

    @Test
    fun searchFieldNotifiesQueryChanges() {
        var query = ""
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state = LibraryUiState(isLoading = false),
                    onQueryChange = { query = it },
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.SEARCH).performTextInput("hello")
        assertThat(query).isEqualTo("hello")
    }

    @Test
    fun searchFieldIsCompactWithPlaceholder() {
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state = LibraryUiState(isLoading = false),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Search title or artist").assertIsDisplayed()
        val bounds = composeRule.onNodeWithTag(LibraryTestTags.SEARCH).getUnclippedBoundsInRoot()
        val height = bounds.bottom.value - bounds.top.value
        assertThat(height).isAtLeast(44f)
        assertThat(height).isAtMost(52f)
        assertThat(LibrarySearchHeight.value).isEqualTo(48f)
    }

    @Test
    fun emptyStateIsShown() {
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state = LibraryUiState(isLoading = false),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.EMPTY).assertIsDisplayed()
    }

    @Test
    fun errorStateRetryIsClickable() {
        var retried = false
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state = LibraryUiState(isLoading = false, isOfflineError = true),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = { retried = true },
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.ERROR).assertIsDisplayed()
        composeRule.onNodeWithTag(LibraryTestTags.RETRY).performClick()
        assertThat(retried).isTrue()
    }

    @Test
    fun loadingStateIsShown() {
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state = LibraryUiState(isLoading = true),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.LOADING).assertIsDisplayed()
    }

    @Test
    fun settingsActionFiresCallback() {
        var settings = 0
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state = LibraryUiState(isLoading = false),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                    onSettingsClick = { settings++ },
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.SETTINGS).performClick()
        assertThat(settings).isEqualTo(1)
    }

    @Test
    fun downloadsActionFiresCallback() {
        var downloads = 0
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state = LibraryUiState(isLoading = false),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                    onDownloadsClick = { downloads++ },
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.DOWNLOADS).performClick()
        assertThat(downloads).isEqualTo(1)
    }

    @Test
    fun refreshAndSettingsAreIconActions() {
        var refresh = 0
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state = LibraryUiState(isLoading = false),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = { refresh++ },
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Refresh catalog").assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Downloads").assertIsDisplayed()
        assertThat(refresh).isEqualTo(1)
    }

    @Test
    fun downloadButtonFiresCallback() {
        var downloadedId: Long? = null
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state =
                        LibraryUiState(
                            songs = listOf(libraryTestSong(id = 1, title = "Alpha")),
                            isLoading = false,
                        ),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                    onDownloadAction = { downloadedId = it.id },
                )
            }
        }

        composeRule.onNodeWithContentDescription("Download").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Download").performClick()
        assertThat(downloadedId).isEqualTo(1L)
    }

    @Test
    fun downloadingRowShowsCancelIcon() {
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state =
                        LibraryUiState(
                            songs = listOf(libraryTestSong(id = 1, title = "Alpha", path = "alpha")),
                            isLoading = false,
                            downloads =
                                mapOf(
                                    "alpha" to
                                        libraryTestDownload(
                                            path = "alpha",
                                            status = DownloadStatus.DOWNLOADING,
                                            progressPercent = 40,
                                        ),
                                ),
                        ),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Cancel").assertIsDisplayed()
        composeRule.onAllNodesWithContentDescription("Download").assertCountEquals(0)
    }

    @Test
    fun completedRowShowsDeleteIcon() {
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state =
                        LibraryUiState(
                            songs = listOf(libraryTestSong(id = 1, title = "Alpha", path = "alpha")),
                            isLoading = false,
                            downloads = mapOf("alpha" to libraryTestDownload(path = "alpha")),
                        ),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Delete").assertIsDisplayed()
    }

    @Test
    fun unavailableOfflineRowIsMarked() {
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    state =
                        LibraryUiState(
                            songs = listOf(libraryTestSong(id = 3, title = "Gamma", path = "gamma")),
                            isLoading = false,
                            isOnline = false,
                        ),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Unavailable offline").assertIsDisplayed()
    }

    @Test
    fun darkThemeAppBarIconsStayFindable() {
        composeRule.setContent {
            LiliTheme(darkTheme = true) {
                LibraryScreen(
                    state = LibraryUiState(songs = emptyList(), isLoading = false),
                    onQueryChange = {},
                    onSortSelected = {},
                    onSortOrderToggle = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }
        composeRule.onNodeWithContentDescription("Refresh catalog").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Downloads").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
    }
}
