package com.anhtuta.lilimusic

import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlayStorePackagingTest {
    @Test
    fun playStorePermissionsAndPackageAreDeclared() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        assertThat(context.packageName).isEqualTo("com.anhtuta.lilimusic")
        val info =
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS,
            )
        val permissions = info.requestedPermissions.orEmpty().toList()
        assertThat(permissions).contains(android.Manifest.permission.INTERNET)
        assertThat(permissions).contains(android.Manifest.permission.POST_NOTIFICATIONS)
        assertThat(permissions).contains(android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)
        assertThat(permissions).contains(android.Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC)
        assertThat(permissions).doesNotContain(android.Manifest.permission.RECORD_AUDIO)
    }

    @Test
    fun releaseProguardRulesCoverPlaybackStack() {
        val xml = LauncherManifestTest.debugMergedManifest().readText()
        assertThat(xml).contains("com.anhtuta.lilimusic.MainActivity")
        val rules = java.io.File("proguard-rules.pro")
        assertThat(rules.isFile()).isTrue()
        val text = rules.readText()
        assertThat(text).contains("Media3")
        assertThat(text).contains("Hilt")
        assertThat(text).contains("Retrofit")
        assertThat(text).contains("Room")
        assertThat(text).contains("kotlinx.serialization")
        assertThat(text).contains("MediaSessionService")
        assertThat(text).contains("retrofit2.http")
    }
}
