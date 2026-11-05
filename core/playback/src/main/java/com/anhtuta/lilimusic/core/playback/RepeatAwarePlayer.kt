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
        if (repeatMode == Player.REPEAT_MODE_ONE) {
            seekTo(0)
        } else {
            super.seekToPreviousMediaItem()
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
        if (repeatMode == Player.REPEAT_MODE_ONE) {
            seekTo(0)
            return
        }
        super.seekToPrevious()
    }
}
