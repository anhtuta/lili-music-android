package com.anhtuta.lilimusic.core.model

import kotlin.math.roundToInt

object PlaybackSpeed {
    const val DEFAULT = 1.0f
    const val MIN = 0.5f
    const val MAX = 2.0f
    const val STEP = 0.1f

    fun coerce(value: Float): Float = roundToStep(value.coerceIn(MIN, MAX))

    fun increase(current: Float): Float = coerce(current + STEP)

    fun decrease(current: Float): Float = coerce(current - STEP)

    fun steps(): List<Float> {
        val values = mutableListOf<Float>()
        var value = MIN
        while (value <= MAX + 0.001f) {
            values += coerce(value)
            value += STEP
        }
        return values
    }

    private fun roundToStep(value: Float): Float = ((value * 10f).roundToInt() / 10f).coerceIn(MIN, MAX)
}
