package com.anhtuta.lilimusic

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.anhtuta.lilimusic.feature.library.LibraryTestTags
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class LauncherActivityTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        WorkManagerTestInitHelper.initializeTestWorkManager(
            context,
            Configuration.Builder().setExecutor(SynchronousExecutor()).build(),
        )
        hiltRule.inject()
    }

    @Test
    fun launchingMergedManifestShowsLibraryOrLoading() {
        val launcherNames =
            LauncherManifestTest.launcherActivityNames(
                LauncherManifestTest.debugMergedManifest().readText(),
            )
        assertThat(launcherNames).containsExactly(MainActivity::class.java.name)

        val scenario =
            try {
                ActivityScenario.launch(MainActivity::class.java)
            } catch (error: Throwable) {
                val chain =
                    generateSequence(error) { it.cause }
                        .joinToString(" -> ") { "${it::class.java.name}: ${it.message}" }
                throw AssertionError("MainActivity launch failed: $chain", error)
            }
        scenario.use {
            it.onActivity { activity ->
                assertThat(activity).isInstanceOf(MainActivity::class.java)
            }
            composeRule.waitUntil(timeoutMillis = 5_000) {
                val loading =
                    composeRule.onAllNodesWithTag(LibraryTestTags.LOADING).fetchSemanticsNodes().isNotEmpty()
                val library = composeRule.onAllNodesWithText("Library").fetchSemanticsNodes().isNotEmpty()
                loading || library
            }
            val loadingNodes = composeRule.onAllNodesWithTag(LibraryTestTags.LOADING).fetchSemanticsNodes()
            if (loadingNodes.isNotEmpty()) {
                composeRule.onAllNodesWithTag(LibraryTestTags.LOADING).onFirst().assertIsDisplayed()
            } else {
                composeRule.onAllNodesWithText("Library").onFirst().assertIsDisplayed()
            }
        }
    }
}
