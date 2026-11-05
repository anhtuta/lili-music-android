@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback

import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LiliPlaybackService : MediaSessionService() {
    @Inject
    lateinit var playerFactory: ExoPlayerFactory

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val exoPlayer = playerFactory.create()
        player = exoPlayer
        mediaSession =
            MediaSession
                .Builder(this, RepeatAwarePlayer(exoPlayer))
                .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val sessionPlayer = mediaSession?.player
        if (sessionPlayer == null || !sessionPlayer.playWhenReady || sessionPlayer.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        player = null
        super.onDestroy()
    }
}
