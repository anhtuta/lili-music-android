package com.anhtuta.lilimusic.core.playback

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

object RecordListenWorkRequests {
    fun constraints(): Constraints =
        Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    fun create(path: String): OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<RecordListenWorker>()
            .setInitialDelay(ListenCountPolicy.DELAY_MS, TimeUnit.MILLISECONDS)
            .setConstraints(constraints())
            .setInputData(workDataOf(RecordListenWorker.KEY_PATH to path))
            .build()
}
