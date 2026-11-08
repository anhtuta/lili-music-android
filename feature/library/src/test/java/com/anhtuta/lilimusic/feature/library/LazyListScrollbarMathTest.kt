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
        assertThat(metrics.heightFraction).isGreaterThan(0.08f)
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
