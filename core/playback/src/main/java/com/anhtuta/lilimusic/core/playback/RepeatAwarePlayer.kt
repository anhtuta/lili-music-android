@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback

import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

internal class RepeatAwarePlayer(
    player: Player,
) : ForwardingPlayer(player) {
    override fun seekToNextMediaItem() {
        if (repeatMode == Player.REPEAT_MODE_ONE) {
            seekTo(0)
        } else {
            super.seekToNextMediaItem()
        }
    }

    override fun seekToPreviousMediaItem() {
        when (
            SkipPreviousPolicy.decide(
                repeatOne = repeatMode == Player.REPEAT_MODE_ONE,
                currentIndex = currentMediaItemIndex,
            )
        ) {
            SkipPreviousAction.ReplayCurrent -> {
                seekTo(0)
                play()
            }
            SkipPreviousAction.StayAtStart -> Unit
            SkipPreviousAction.GoToPrevious -> super.seekToPreviousMediaItem()
        }
    }

    override fun seekToNext() {
        if (repeatMode == Player.REPEAT_MODE_ONE) {
            seekTo(0)
            play()
            return
        }
        super.seekToNext()
    }

    override fun seekToPrevious() {
        when (
            SkipPreviousPolicy.decide(
                repeatOne = repeatMode == Player.REPEAT_MODE_ONE,
                currentIndex = currentMediaItemIndex,
            )
        ) {
            SkipPreviousAction.ReplayCurrent -> {
                seekTo(0)
                play()
            }
            SkipPreviousAction.StayAtStart -> Unit
            // Media3's seekToPrevious restarts the current item after 3s; web always
            // goes to the previous track, so skip that threshold.
            SkipPreviousAction.GoToPrevious -> seekToPreviousMediaItem()
        }
    }
}
