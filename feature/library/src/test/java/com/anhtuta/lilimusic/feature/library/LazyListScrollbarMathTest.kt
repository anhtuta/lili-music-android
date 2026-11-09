package com.anhtuta.lilimusic.feature.library

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LazyListScrollbarMathTest {
    @Test
    fun thumbVisibleWhenListIsScrollable() {
        val metrics =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 50,
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = 0,
                firstVisibleItemSize = 72,
                canScrollForward = true,
                canScrollBackward = false,
            )
        assertThat(metrics.visible).isTrue()
        assertThat(metrics.topFraction).isWithin(0.001f).of(0f)
        assertThat(metrics.heightFraction).isWithin(0.001f).of(0.1f)
    }

    @Test
    fun thumbTracksPartialItemScrollOffset() {
        val atStart =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 50,
                firstVisibleItemIndex = 5,
                firstVisibleItemScrollOffset = 0,
                firstVisibleItemSize = 80,
                canScrollForward = true,
                canScrollBackward = true,
            )
        val midItem =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 50,
                firstVisibleItemIndex = 5,
                firstVisibleItemScrollOffset = 40,
                firstVisibleItemSize = 80,
                canScrollForward = true,
                canScrollBackward = true,
            )

        assertThat(midItem.topFraction).isGreaterThan(atStart.topFraction)
        assertThat(midItem.heightFraction).isWithin(0.001f).of(atStart.heightFraction)
    }

    @Test
    fun thumbHeightStaysStableAcrossPositions() {
        val middle =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 50,
                firstVisibleItemIndex = 20,
                firstVisibleItemScrollOffset = 24,
                firstVisibleItemSize = 72,
                canScrollForward = true,
                canScrollBackward = true,
            )
        val end =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 50,
                firstVisibleItemIndex = 49,
                firstVisibleItemScrollOffset = 0,
                firstVisibleItemSize = 72,
                canScrollForward = false,
                canScrollBackward = true,
            )

        assertThat(middle.heightFraction).isWithin(0.001f).of(end.heightFraction)
        assertThat(end.topFraction).isWithin(0.001f).of(0.9f)
    }

    @Test
    fun bottomTouchTargetsFinalItem() {
        assertThat(LazyListScrollbarMath.targetIndexForFraction(totalItems = 50, fraction = 1f))
            .isEqualTo(49)
    }

    @Test
    fun thumbHiddenWhenNothingScrolls() {
        val metrics =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 3,
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = 0,
                firstVisibleItemSize = 72,
                canScrollForward = false,
                canScrollBackward = false,
            )
        assertThat(metrics.visible).isFalse()
    }
}
