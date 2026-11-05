package com.anhtuta.lilimusic.feature.player

object PlayerTimeFormatter {
    fun format(positionMs: Long): String {
        val totalSeconds = (positionMs / 1000L).coerceAtLeast(0L)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
