package com.anhtuta.lilimusic

import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import androidx.test.core.app.ApplicationProvider
import com.anhtuta.lilimusic.core.playback.LiliPlaybackService
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlaybackManifestTest {
    @Test
    fun playbackServiceUsesMediaPlaybackForegroundType() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val info =
            context.packageManager.getServiceInfo(
                ComponentName(context, LiliPlaybackService::class.java),
                PackageManager.GET_META_DATA,
            )
        assertThat(info.foregroundServiceType)
            .isEqualTo(ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
    }

    @Test
    fun postNotificationsPermissionIsDeclared() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val info =
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS,
            )
        assertThat(info.requestedPermissions).asList().contains(android.Manifest.permission.POST_NOTIFICATIONS)
    }
}
