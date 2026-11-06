package com.anhtuta.lilimusic.core.playback

/**
 * Previous-track decision matching web `Player.js` `prevSong`.
 *
 * There is no “restart current if position > 3s” rule. Repeat-one replays the
 * same item. On the first track, previous is a no-op (keep position) and the UI
 * shows the web toast equivalent.
 */
enum class SkipPreviousAction {
    ReplayCurrent,
    GoToPrevious,
    StayAtStart,
}

object SkipPreviousPolicy {
    fun decide(
        repeatOne: Boolean,
        currentIndex: Int,
    ): SkipPreviousAction =
        when {
            repeatOne -> SkipPreviousAction.ReplayCurrent
            currentIndex <= 0 -> SkipPreviousAction.StayAtStart
            else -> SkipPreviousAction.GoToPrevious
        }
}
