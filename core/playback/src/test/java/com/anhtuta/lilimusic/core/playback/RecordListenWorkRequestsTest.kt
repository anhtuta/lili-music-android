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
        val request = RecordListenWorkRequests.create("hello artist")

        assertThat(request.workSpec.initialDelay).isEqualTo(ListenCountPolicy.DELAY_MS)
        assertThat(request.workSpec.constraints.requiredNetworkType).isEqualTo(NetworkType.CONNECTED)
        assertThat(request.workSpec.input.getString(RecordListenWorker.KEY_PATH)).isEqualTo("hello artist")
    }
}
