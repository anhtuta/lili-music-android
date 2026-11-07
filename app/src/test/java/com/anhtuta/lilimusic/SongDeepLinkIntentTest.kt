package com.anhtuta.lilimusic

import android.content.Intent
import android.net.Uri
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SongDeepLinkIntentTest {
    @Test
    fun hashWebUrlBecomesCustomScheme() {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://anhtuta.github.io/lili-player/#/bai-hat/hello-artist"))
        MainActivity.applySongDeepLink(intent)
        assertThat(intent.data.toString()).isEqualTo("lilimusic://bai-hat/hello-artist")
    }

    @Test
    fun httpsPathStyleBecomesCustomScheme() {
        val intent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://blog.nenmongvietnam.com/bai-hat/hello-artist"),
            )
        MainActivity.applySongDeepLink(intent)
        assertThat(intent.data.toString()).isEqualTo("lilimusic://bai-hat/hello-artist")
    }

    @Test
    fun unrelatedUriIsLeftAlone() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.nenmongvietnam.com/laravel/public"))
        MainActivity.applySongDeepLink(intent)
        assertThat(intent.data.toString()).isEqualTo("https://blog.nenmongvietnam.com/laravel/public")
    }
}
