package com.anhtuta.lilimusic.core.playback

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
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
            val request = RecordListenWorkRequests.create(path)
            workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }

        companion object {
            const val UNIQUE_WORK_NAME = "record-listen"
        }
    }
