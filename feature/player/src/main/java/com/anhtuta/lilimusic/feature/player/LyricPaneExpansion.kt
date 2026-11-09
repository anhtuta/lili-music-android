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

    /** 0 = controls visible; 1 = lyrics expanded / controls hidden. */
    fun dragProgress(
        expanded: Boolean,
        dragY: Float,
        thresholdPx: Float = DRAG_THRESHOLD_PX,
    ): Float {
        val origin = if (expanded) 1f else 0f
        return (origin + dragY / thresholdPx).coerceIn(0f, 1f)
    }
}
