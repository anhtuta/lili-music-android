package com.anhtuta.lilimusic.feature.library

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LazyListScrollbarMathTest {
    @Test
    fun thumbVisibleWhenListIsScrollable() {
        val metrics =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 50,
                firstVisibleIndex = 0,
                lastVisibleIndex = 8,
                canScrollForward = true,
                canScrollBackward = false,
            )
        assertThat(metrics.visible).isTrue()
        assertThat(metrics.topFraction).isWithin(0.001f).of(0f)
        assertThat(metrics.heightFraction).isWithin(0.001f).of(0.1f)
    }

    @Test
    fun thumbHeightAndBoundsStayStableAtListEnd() {
        val middle =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 50,
                firstVisibleIndex = 20,
                lastVisibleIndex = 28,
                canScrollForward = true,
                canScrollBackward = true,
            )
        val end =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 50,
                firstVisibleIndex = 41,
                lastVisibleIndex = 49,
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
    fun thumbHiddenWhenEverythingFits() {
        val metrics =
            LazyListScrollbarMath.thumbMetrics(
                totalItems = 3,
                firstVisibleIndex = 0,
                lastVisibleIndex = 2,
                canScrollForward = false,
                canScrollBackward = false,
            )
        assertThat(metrics.visible).isFalse()
    }
}
