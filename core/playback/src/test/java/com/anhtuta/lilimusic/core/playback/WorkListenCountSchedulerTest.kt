package com.anhtuta.lilimusic.core.playback

import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WorkListenCountSchedulerTest {
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val config =
            Configuration
                .Builder()
                .setExecutor(SynchronousExecutor())
                .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Test
    fun schedulesUniqueDelayedWorkForPath() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val scheduler = WorkListenCountScheduler(context)

        scheduler.apply(ListenCountDecision(cancelPending = false, schedulePath = "hello artist", delayMs = 30_000L))

        val infos =
            WorkManager
                .getInstance(context)
                .getWorkInfosForUniqueWork(WorkListenCountScheduler.UNIQUE_WORK_NAME)
                .get()
        assertThat(infos).isNotEmpty()
        assertThat(infos.single().state.isFinished).isFalse()
    }

    @Test
    fun cancelPendingRemovesWork() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val scheduler = WorkListenCountScheduler(context)
        scheduler.apply(ListenCountDecision(cancelPending = false, schedulePath = "hello"))
        scheduler.apply(ListenCountDecision(cancelPending = true, schedulePath = null))

        val infos =
            WorkManager
                .getInstance(context)
                .getWorkInfosForUniqueWork(WorkListenCountScheduler.UNIQUE_WORK_NAME)
                .get()
        assertThat(infos.all { it.state.isFinished }).isTrue()
    }
}
