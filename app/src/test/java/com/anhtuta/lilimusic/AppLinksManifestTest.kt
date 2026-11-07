package com.anhtuta.lilimusic

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AppLinksManifestTest {
    @Test
    fun mainActivityDeclaresSongViewFilters() {
        val xml = LauncherManifestTest.debugMergedManifest().readText()
        val main =
            Regex("""<activity\b[\s\S]*?</activity>""")
                .findAll(xml)
                .map { it.value }
                .first { it.contains("com.anhtuta.lilimusic.MainActivity") }
        assertThat(main).contains("android.intent.action.VIEW")
        assertThat(main).contains("android.intent.category.BROWSABLE")
        assertThat(main).contains("lilimusic")
        assertThat(main).contains("blog.nenmongvietnam.com")
        assertThat(main).contains("anhtuta.github.io")
        assertThat(main).contains("/bai-hat")
        assertThat(main).contains("/lili-player/bai-hat")
        assertThat(main).contains("android:autoVerify=\"true\"")
        assertThat(main).contains("android:launchMode=\"singleTop\"")
        assertThat(LauncherManifestTest.launcherActivityNames(xml))
            .containsExactly(MainActivity::class.java.name)
    }
}
