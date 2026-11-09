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
        firstVisibleItemIndex: Int,
        firstVisibleItemScrollOffset: Int,
        canScrollForward: Boolean,
        canScrollBackward: Boolean,
    ): ThumbMetrics {
        val firstItemSize =
            layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == firstVisibleItemIndex }
                ?.size
                ?: layoutInfo.visibleItemsInfo.firstOrNull()?.size
                ?: 1
        return thumbMetrics(
            totalItems = layoutInfo.totalItemsCount,
            firstVisibleItemIndex = firstVisibleItemIndex,
            firstVisibleItemScrollOffset = firstVisibleItemScrollOffset,
            firstVisibleItemSize = firstItemSize,
            canScrollForward = canScrollForward,
            canScrollBackward = canScrollBackward,
        )
    }

    fun thumbMetrics(
        totalItems: Int,
        firstVisibleIndex: Int,
        lastVisibleIndex: Int,
        canScrollForward: Boolean,
        canScrollBackward: Boolean,
    ): ThumbMetrics =
        thumbMetrics(
            totalItems = totalItems,
            firstVisibleItemIndex = firstVisibleIndex,
            firstVisibleItemScrollOffset = 0,
            firstVisibleItemSize = 1,
            canScrollForward = canScrollForward,
            canScrollBackward = canScrollBackward,
        )

    fun thumbMetrics(
        totalItems: Int,
        firstVisibleItemIndex: Int,
        firstVisibleItemScrollOffset: Int,
        firstVisibleItemSize: Int,
        canScrollForward: Boolean,
        canScrollBackward: Boolean,
    ): ThumbMetrics {
        if (totalItems <= 0) {
            return ThumbMetrics(topFraction = 0f, heightFraction = 0f, visible = false)
        }
        val heightFraction = StableThumbHeightFraction
        val maxIndex = (totalItems - 1).coerceAtLeast(1)
        val indexProgress =
            if (maxIndex == 0) {
                0f
            } else {
                val offsetInItem =
                    firstVisibleItemScrollOffset.toFloat() /
                        firstVisibleItemSize.coerceAtLeast(1)
                ((firstVisibleItemIndex + offsetInItem) / maxIndex).coerceIn(0f, 1f)
            }
        val topFraction = (indexProgress * (1f - heightFraction)).coerceIn(0f, 1f - heightFraction)
        val scrollable = canScrollForward || canScrollBackward
        return ThumbMetrics(
            topFraction = topFraction,
            heightFraction = heightFraction,
            visible = scrollable,
        )
    }

    fun targetIndexForFraction(totalItems: Int, fraction: Float): Int {
        val maxIndex = (totalItems - 1).coerceAtLeast(0)
        return (fraction.coerceIn(0f, 1f) * maxIndex).roundToInt()
    }
}
