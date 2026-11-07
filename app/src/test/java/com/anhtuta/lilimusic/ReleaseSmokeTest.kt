package com.anhtuta.lilimusic

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.junit.Test

class ReleaseSmokeTest {
    @Test
    fun smokeScriptGatesMinifiedReleaseApk() {
        val script =
            File("../scripts/test-release-smoke.sh").takeIf { it.isFile }
                ?: File("scripts/test-release-smoke.sh")
        assertThat(script.isFile).isTrue()
        val text = script.readText()
        assertThat(text).contains(":app:assembleRelease")
        assertThat(text).contains("mapping.txt")
        assertThat(text).contains("com.anhtuta.lilimusic")
        assertThat(text).contains("MainActivity")
        assertThat(text).contains("RECORD_AUDIO")
        assertThat(text).contains("FOREGROUND_SERVICE_MEDIA_PLAYBACK")
        assertThat(text).contains("aapt")
    }
}
