@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoPlayerFactory
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun create(): ExoPlayer {
            val audioAttributes =
                AudioAttributes
                    .Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build()
            return ExoPlayer
                .Builder(context)
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .setWakeMode(C.WAKE_MODE_NETWORK)
                .setSeekBackIncrementMs(PlaybackSeek.INCREMENT_MS)
                .setSeekForwardIncrementMs(PlaybackSeek.INCREMENT_MS)
                .build()
        }
    }
