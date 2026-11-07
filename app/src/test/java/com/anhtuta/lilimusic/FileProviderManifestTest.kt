package com.anhtuta.lilimusic

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FileProviderManifestTest {
    @Test
    fun debugManifestDeclaresLyricCardFileProvider() {
        val xml = LauncherManifestTest.debugMergedManifest().readText()
        assertThat(xml).contains("androidx.core.content.FileProvider")
        assertThat(xml).contains("com.anhtuta.lilimusic.fileprovider")
        assertThat(xml).contains("@xml/file_paths")
        assertThat(xml).doesNotContain("android.permission.RECORD_AUDIO")
    }
}
