package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val ScrollbarThumbWidth = 10.dp
private val ScrollbarTouchTargetWidth = 48.dp
private const val ScrollbarHideDelayMillis = 600L

@Composable
internal fun LibraryLazyScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    var scrollbarVisible by remember { mutableStateOf(false) }
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collectLatest { isScrolling ->
                if (isScrolling) {
                    scrollbarVisible = true
                } else {
                    delay(ScrollbarHideDelayMillis)
                    scrollbarVisible = false
                }
            }
    }
    val layoutInfo = listState.layoutInfo
    val metrics =
        LazyListScrollbarMath.thumbMetrics(
            layoutInfo = layoutInfo,
            canScrollForward = listState.canScrollForward,
            canScrollBackward = listState.canScrollBackward,
        )
    if (!metrics.visible || !scrollbarVisible) {
        return
    }
    val viewportPx = layoutInfo.viewportSize.height.toFloat()
    val thumbTopPx = metrics.topFraction * viewportPx
    val scope = rememberCoroutineScope()

    fun scrollToFraction(fraction: Float) {
        scope.launch {
            listState.scrollToItem(
                LazyListScrollbarMath.targetIndexForFraction(
                    totalItems = layoutInfo.totalItemsCount,
                    fraction = fraction,
                ),
            )
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .width(ScrollbarTouchTargetWidth)
                .testTag(LibraryTestTags.SCROLLBAR),
        contentAlignment = Alignment.TopEnd,
    ) {
        Box(
            modifier =
                Modifier
                    .offset { IntOffset(0, thumbTopPx.roundToInt()) }
                    .width(LibraryChromeInsets.ScrollbarTrack)
                    .fillMaxHeight(metrics.heightFraction),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier =
                    Modifier
                        .width(ScrollbarThumbWidth)
                        .fillMaxHeight()
                        .testTag(LibraryTestTags.SCROLLBAR_THUMB)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                            shape =
                                RoundedCornerShape(
                                    topStartPercent = 50,
                                    bottomStartPercent = 50,
                                ),
                        ),
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(ScrollbarTouchTargetWidth)
                    .pointerInput(listState, layoutInfo.totalItemsCount) {
                        detectTapGestures { position ->
                            scrollToFraction(position.y / viewportPx)
                        }
                    }
                    .pointerInput(listState, layoutInfo.totalItemsCount) {
                        detectDragGestures(
                            onDragStart = { position ->
                                scrollToFraction(position.y / viewportPx)
                            },
                        ) { change, _ ->
                            change.consume()
                            scrollToFraction(change.position.y / viewportPx)
                        }
                    },
        )
    }
}
