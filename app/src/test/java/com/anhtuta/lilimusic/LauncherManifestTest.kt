package com.anhtuta.lilimusic

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.junit.Test

class LauncherManifestTest {
    @Test
    fun debugApkHasExactlyOneLauncherAndItIsMainActivity() {
        val names = launcherActivityNames(debugMergedManifest().readText())
        assertThat(names).containsExactly(MainActivity::class.java.name)
        assertThat(names).doesNotContain("androidx.activity.ComponentActivity")
    }

    companion object {
        fun debugMergedManifest(): File {
            val file =
                File("build/intermediates/merged_manifest/debug/processDebugMainManifest/AndroidManifest.xml")
            assertThat(file.exists()).isTrue()
            return file
        }

        fun launcherActivityNames(xml: String): List<String> {
            val activity = Regex("""<activity\b[\s\S]*?</activity>""")
            return activity
                .findAll(xml)
                .mapNotNull { match ->
                    val block = match.value
                    if (!block.contains("android.intent.category.LAUNCHER")) {
                        return@mapNotNull null
                    }
                    Regex("""android:name="([^"]+)"""").find(block)?.groupValues?.get(1)
                }.toList()
        }
    }
}
