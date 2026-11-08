package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState

internal object LyricPaneScroll {
    fun shouldAutoScroll(userDragging: Boolean): Boolean = !userDragging

    fun centerDelta(
        viewportStart: Int,
        viewportEnd: Int,
        itemOffset: Int,
        itemSize: Int,
    ): Int {
        val viewportCenter = (viewportStart + viewportEnd) / 2
        val itemCenter = itemOffset + itemSize / 2
        return itemCenter - viewportCenter
    }
}

internal suspend fun LazyListState.animateScrollItemToCenter(index: Int) {
    if (layoutInfo.visibleItemsInfo.none { it.index == index }) {
        animateScrollToItem(index)
    }
    val item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index } ?: return
    val delta =
        LyricPaneScroll.centerDelta(
            viewportStart = layoutInfo.viewportStartOffset,
            viewportEnd = layoutInfo.viewportEndOffset,
            itemOffset = item.offset,
            itemSize = item.size,
        )
    if (delta != 0) {
        animateScrollBy(delta.toFloat())
    }
}
