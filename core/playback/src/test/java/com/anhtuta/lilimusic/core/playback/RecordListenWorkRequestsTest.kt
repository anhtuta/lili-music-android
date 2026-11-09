package com.anhtuta.lilimusic.core.playback

import androidx.work.NetworkType
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RecordListenWorkRequestsTest {
    @Test
    fun listenWorkWaitsSixtySecondsAndRequiresNetwork() {
        val request = RecordListenWorkRequests.create("hello artist", delayMs = 45_000L)

        assertThat(request.workSpec.initialDelay).isEqualTo(45_000L)
        assertThat(request.workSpec.constraints.requiredNetworkType).isEqualTo(NetworkType.CONNECTED)
        assertThat(request.workSpec.input.getString(RecordListenWorker.KEY_PATH)).isEqualTo("hello artist")
    }
}
