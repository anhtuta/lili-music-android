package com.anhtuta.lilimusic.core.playback

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.anhtuta.lilimusic.core.playback.download.DownloadInfrastructure
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExoPlayerFactoryTest {
    @Test
    fun seekIncrementsAreFiveSeconds() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val player = ExoPlayerFactory(context, DownloadInfrastructure(context)).create()
        try {
            assertThat(player.seekBackIncrement).isEqualTo(PlaybackSeek.INCREMENT_MS)
            assertThat(player.seekForwardIncrement).isEqualTo(PlaybackSeek.INCREMENT_MS)
        } finally {
            player.release()
        }
    }
}
