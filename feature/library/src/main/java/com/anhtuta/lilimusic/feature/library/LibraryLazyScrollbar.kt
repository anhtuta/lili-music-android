package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private val ScrollbarThumbWidth = 10.dp

@Composable
internal fun LibraryLazyScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val layoutInfo = listState.layoutInfo
    val metrics =
        LazyListScrollbarMath.thumbMetrics(
            layoutInfo = layoutInfo,
            canScrollForward = listState.canScrollForward,
            canScrollBackward = listState.canScrollBackward,
        )
    if (!metrics.visible) {
        return
    }
    val viewportPx = layoutInfo.viewportSize.height.toFloat()
    val thumbHeightPx = metrics.heightFraction * viewportPx
    val thumbTopPx = metrics.topFraction * viewportPx
    val scope = rememberCoroutineScope()
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .width(LibraryChromeInsets.ScrollbarTrack)
                .testTag(LibraryTestTags.SCROLLBAR),
        contentAlignment = Alignment.TopEnd,
    ) {
        Box(
            modifier =
                Modifier
                    .offset { IntOffset(0, thumbTopPx.roundToInt()) }
                    .width(LibraryChromeInsets.ScrollbarTrack)
                    .fillMaxHeight(metrics.heightFraction)
                    .pointerInput(listState, layoutInfo.totalItemsCount) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaIndex =
                                (dragAmount.y / viewportPx * layoutInfo.totalItemsCount).roundToInt()
                            if (deltaIndex != 0) {
                                val target =
                                    (listState.firstVisibleItemIndex + deltaIndex)
                                        .coerceIn(0, (layoutInfo.totalItemsCount - 1).coerceAtLeast(0))
                                scope.launch {
                                    listState.scrollToItem(target)
                                }
                            }
                        }
                    },
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier =
                    Modifier
                        .width(ScrollbarThumbWidth)
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(5.dp),
                        ),
            )
        }
    }
}
