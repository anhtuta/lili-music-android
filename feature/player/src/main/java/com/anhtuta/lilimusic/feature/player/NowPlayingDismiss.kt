package com.anhtuta.lilimusic.feature.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

object NowPlayingDismiss {
    const val DISTANCE_THRESHOLD_PX = 140f
    const val FLING_THRESHOLD_PX_PER_SEC = 2200f

    fun shouldDismiss(
        draggedDownPx: Float,
        flingY: Float = 0f,
        distanceThresholdPx: Float = DISTANCE_THRESHOLD_PX,
        flingThreshold: Float = FLING_THRESHOLD_PX_PER_SEC,
    ): Boolean = draggedDownPx >= distanceThresholdPx || flingY >= flingThreshold
}

@Composable
fun rememberNowPlayingDismissScroll(
    enabled: Boolean,
    onDismiss: () -> Unit,
): NestedScrollConnection {
    return remember(enabled, onDismiss) {
        object : NestedScrollConnection {
            private var dragged = 0f

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!enabled || source != NestedScrollSource.UserInput) {
                    return Offset.Zero
                }
                if (available.y > 0f) {
                    dragged += available.y
                    if (NowPlayingDismiss.shouldDismiss(dragged)) {
                        dragged = 0f
                        onDismiss()
                    }
                } else if (available.y < 0f) {
                    dragged = 0f
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity {
                if (enabled && NowPlayingDismiss.shouldDismiss(dragged, available.y)) {
                    dragged = 0f
                    onDismiss()
                } else {
                    dragged = 0f
                }
                return Velocity.Zero
            }
        }
    }
}
