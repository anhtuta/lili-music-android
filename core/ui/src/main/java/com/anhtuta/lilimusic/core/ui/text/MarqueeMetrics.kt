package com.anhtuta.lilimusic.core.ui.text

internal object MarqueeMetrics {
    const val SPEED_DP_PER_SEC = 35f
    const val START_PAUSE_MS = 800L
    const val END_PAUSE_MS = 1_750L
    const val RESUME_PAUSE_MS = 600L

    fun durationMs(
        distancePx: Float,
        speedPxPerSec: Float,
    ): Int {
        if (distancePx <= 0f || speedPxPerSec <= 0f) return 0
        return ((distancePx / speedPxPerSec) * 1_000f).toInt().coerceAtLeast(1)
    }

    fun shouldAnimate(
        maxScroll: Int,
        userDragging: Boolean,
    ): Boolean = maxScroll > 0 && !userDragging
}
