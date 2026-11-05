package com.anhtuta.lilimusic.core.playback

import androidx.work.ListenableWorker
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RecordListenWorkTest {
    @Test
    fun blankPathFails() =
        runTest {
            val result = RecordListenWork.run(path = null) { Result.success(Unit) }

            assertThat(result).isEqualTo(ListenableWorker.Result.failure())
        }

    @Test
    fun successfulRecordSucceeds() =
        runTest {
            var recorded: String? = null
            val result =
                RecordListenWork.run("hello artist") { path ->
                    recorded = path
                    Result.success(Unit)
                }

            assertThat(recorded).isEqualTo("hello artist")
            assertThat(result).isEqualTo(ListenableWorker.Result.success())
        }

    @Test
    fun failedRecordRetries() =
        runTest {
            val result = RecordListenWork.run("hello") { Result.failure(IllegalStateException("offline")) }

            assertThat(result).isEqualTo(ListenableWorker.Result.retry())
        }
}
