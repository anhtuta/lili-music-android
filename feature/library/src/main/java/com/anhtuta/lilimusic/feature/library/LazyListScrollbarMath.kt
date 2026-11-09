package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.lazy.LazyListLayoutInfo
import kotlin.math.roundToInt

internal object LazyListScrollbarMath {
    private const val StableThumbHeightFraction = 0.1f

    data class ThumbMetrics(
        val topFraction: Float,
        val heightFraction: Float,
        val visible: Boolean,
    )

    fun thumbMetrics(
        layoutInfo: LazyListLayoutInfo,
        canScrollForward: Boolean,
        canScrollBackward: Boolean,
    ): ThumbMetrics =
        thumbMetrics(
            totalItems = layoutInfo.totalItemsCount,
            firstVisibleIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0,
            lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0,
            canScrollForward = canScrollForward,
            canScrollBackward = canScrollBackward,
        )

    fun thumbMetrics(
        totalItems: Int,
        firstVisibleIndex: Int,
        lastVisibleIndex: Int,
        canScrollForward: Boolean,
        canScrollBackward: Boolean,
    ): ThumbMetrics {
        if (totalItems <= 0 || lastVisibleIndex < firstVisibleIndex) {
            return ThumbMetrics(topFraction = 0f, heightFraction = 0f, visible = false)
        }
        val visibleSpan = (lastVisibleIndex - firstVisibleIndex + 1).coerceAtLeast(1)
        val heightFraction = StableThumbHeightFraction
        val maxFirstVisibleIndex = (totalItems - visibleSpan).coerceAtLeast(1)
        val topFraction =
            when {
                !canScrollBackward -> 0f
                !canScrollForward -> 1f - heightFraction
                else -> {
                    (firstVisibleIndex.toFloat() / maxFirstVisibleIndex.toFloat())
                        .coerceIn(0f, 1f - heightFraction)
                }
            }
        val scrollable = canScrollForward || canScrollBackward
        return ThumbMetrics(
            topFraction = topFraction,
            heightFraction = heightFraction,
            visible = scrollable && totalItems > visibleSpan,
        )
    }

    fun targetIndexForFraction(totalItems: Int, fraction: Float): Int {
        val maxIndex = (totalItems - 1).coerceAtLeast(0)
        return (fraction.coerceIn(0f, 1f) * maxIndex).roundToInt()
    }
}
