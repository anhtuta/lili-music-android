package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.lazy.LazyListLayoutInfo

internal object LazyListScrollbarMath {
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
        val heightFraction = (visibleSpan.toFloat() / totalItems.toFloat()).coerceIn(0.08f, 1f)
        val topFraction =
            (firstVisibleIndex.toFloat() / totalItems.toFloat())
                .coerceIn(0f, 1f - heightFraction)
        val scrollable = canScrollForward || canScrollBackward
        return ThumbMetrics(
            topFraction = topFraction,
            heightFraction = heightFraction,
            visible = scrollable && totalItems > visibleSpan,
        )
    }
}
