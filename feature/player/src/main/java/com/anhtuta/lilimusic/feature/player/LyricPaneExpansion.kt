package com.anhtuta.lilimusic.feature.player

object LyricPaneExpansion {
    const val DRAG_THRESHOLD_PX = 48f

    fun afterVerticalDrag(
        expanded: Boolean,
        dragY: Float,
        thresholdPx: Float = DRAG_THRESHOLD_PX,
    ): Boolean =
        when {
            dragY > thresholdPx -> true
            dragY < -thresholdPx -> false
            else -> expanded
        }
}
