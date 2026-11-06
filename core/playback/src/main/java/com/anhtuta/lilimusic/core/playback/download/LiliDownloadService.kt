@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback.download

import android.app.Notification
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Scheduler
import com.anhtuta.lilimusic.core.playback.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LiliDownloadService :
    DownloadService(
        FOREGROUND_NOTIFICATION_ID,
        DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
        CHANNEL_ID,
        R.string.lili_download_notification_channel_name,
        R.string.lili_download_notification_channel_description,
    ) {
    @Inject
    lateinit var infrastructure: DownloadInfrastructure

    @Inject
    lateinit var notificationHelper: DownloadNotificationHelper

    override fun getDownloadManager(): DownloadManager = infrastructure.downloadManager

    override fun getScheduler(): Scheduler = PlatformScheduler(this, JOB_ID)

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int,
    ): Notification =
        notificationHelper.buildProgressNotification(
            this,
            android.R.drawable.stat_sys_download,
            // contentIntent=
            null,
            // message=
            null,
            downloads,
            notMetRequirements,
        )

    companion object {
        const val CHANNEL_ID = "lili_downloads"
        const val FOREGROUND_NOTIFICATION_ID = 2
        const val JOB_ID = 2
    }
}
