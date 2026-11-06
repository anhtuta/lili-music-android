package com.anhtuta.lilimusic.core.playback

/**
 * Previous-track decision matching web `Player.js` `prevSong`.
 *
 * There is no “restart current if position > 3s” rule. Repeat-one replays the
 * same item; otherwise previous always means the prior queue index.
 */
enum class SkipPreviousAction {
    ReplayCurrent,
    GoToPrevious,
}

object SkipPreviousPolicy {
    fun decide(repeatOne: Boolean): SkipPreviousAction =
        if (repeatOne) {
            SkipPreviousAction.ReplayCurrent
        } else {
            SkipPreviousAction.GoToPrevious
        }
}
