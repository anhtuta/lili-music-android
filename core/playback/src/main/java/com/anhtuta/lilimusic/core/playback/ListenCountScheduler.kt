package com.anhtuta.lilimusic.core.playback

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

interface ListenCountScheduler {
    fun apply(decision: ListenCountDecision)
}

@Singleton
class WorkListenCountScheduler
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : ListenCountScheduler {
        private val workManager = WorkManager.getInstance(context)

        override fun apply(decision: ListenCountDecision) {
            if (decision.cancelPending) {
                workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
            }
            val path = decision.schedulePath ?: return
            val request =
                OneTimeWorkRequestBuilder<RecordListenWorker>()
                    .setInitialDelay(ListenCountPolicy.DELAY_MS, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(RecordListenWorker.KEY_PATH to path))
                    .build()
            workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }

        companion object {
            const val UNIQUE_WORK_NAME = "record-listen"
        }
    }
