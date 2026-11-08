package com.anhtuta.lilimusic.core.ui.text

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MarqueeTextTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun durationUsesSpeedBasedLinearMs() {
        assertThat(MarqueeMetrics.SPEED_DP_PER_SEC).isAtLeast(30f)
        assertThat(MarqueeMetrics.SPEED_DP_PER_SEC).isAtMost(40f)
        assertThat(MarqueeMetrics.END_PAUSE_MS).isAtLeast(1_500L)
        assertThat(MarqueeMetrics.END_PAUSE_MS).isAtMost(2_000L)
        assertThat(MarqueeMetrics.durationMs(350f, 35f)).isEqualTo(10_000)
        assertThat(MarqueeMetrics.durationMs(0f, 35f)).isEqualTo(0)
    }

    @Test
    fun shouldAnimateOnlyWhenOverflowingAndNotDragging() {
        assertThat(MarqueeMetrics.shouldAnimate(maxScroll = 0, userDragging = false)).isFalse()
        assertThat(MarqueeMetrics.shouldAnimate(maxScroll = 40, userDragging = true)).isFalse()
        assertThat(MarqueeMetrics.shouldAnimate(maxScroll = 40, userDragging = false)).isTrue()
    }

    @Test
    fun shortTextDoesNotScroll() {
        val holder = ScrollHolder()
        composeRule.setContent {
            LiliTheme {
                val scroll = rememberScrollState()
                holder.state = scroll
                Box(modifier = Modifier.width(240.dp)) {
                    MarqueeText(
                        text = "Hi",
                        style = MaterialTheme.typography.titleMedium,
                        scrollState = scroll,
                    )
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(3_000)
        composeRule.waitForIdle()
        val scroll = requireNotNull(holder.state)
        assertThat(scroll.maxValue).isEqualTo(0)
        assertThat(scroll.value).isEqualTo(0)
    }

    @Test
    fun longTextScrollValueIncreasesOverTime() {
        val holder = ScrollHolder()
        composeRule.setContent {
            LiliTheme {
                val scroll = rememberScrollState()
                holder.state = scroll
                Box(modifier = Modifier.width(12.dp)) {
                    MarqueeText(
                        text = LONG_TITLE,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 48.sp),
                        scrollState = scroll,
                    )
                }
            }
        }
        composeRule.waitForIdle()
        val scroll = requireNotNull(holder.state)
        assertThat(scroll.maxValue).isGreaterThan(0)
        composeRule.waitUntil(timeoutMillis = 5_000) { scroll.value > 0 }
        assertThat(scroll.value).isGreaterThan(0)
    }

    @Test
    fun manualSwipeScrollsOverflowingText() {
        val holder = ScrollHolder()
        composeRule.setContent {
            LiliTheme {
                val scroll = rememberScrollState()
                holder.state = scroll
                Box(
                    modifier =
                        Modifier
                            .width(32.dp)
                            .height(56.dp),
                ) {
                    MarqueeText(
                        text = LONG_TITLE,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 48.sp),
                        scrollState = scroll,
                        modifier = Modifier.testTag("marquee"),
                    )
                }
            }
        }
        composeRule.waitForIdle()
        val scroll = requireNotNull(holder.state)
        assertThat(scroll.maxValue).isGreaterThan(0)
        composeRule.onNodeWithTag("marquee").performTouchInput {
            down(center)
            moveBy(Offset(-80f, 0f))
            up()
        }
        composeRule.waitForIdle()
        assertThat(scroll.value).isGreaterThan(0)
    }

    private companion object {
        const val LONG_TITLE =
            "All Falls Down by Alan Walker, Noah Cyrus, Digital Farm Animals and still more words"
    }
}

private class ScrollHolder {
    var state: ScrollState? = null
}
