package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SongDeepLinkTest {
    @Test
    fun parsesCustomScheme() {
        assertThat(SongDeepLink.songPath("lilimusic://bai-hat/hello-artist"))
            .isEqualTo("hello-artist")
        assertThat(SongDeepLink.canonicalViewUri("lilimusic://bai-hat/hello-artist"))
            .isEqualTo("lilimusic://bai-hat/hello-artist")
    }

    @Test
    fun parsesHttpsApiHost() {
        assertThat(
            SongDeepLink.songPath("https://blog.nenmongvietnam.com/bai-hat/hello-artist"),
        ).isEqualTo("hello-artist")
    }

    @Test
    fun parsesGithubPagesPathAndHash() {
        assertThat(
            SongDeepLink.songPath("https://anhtuta.github.io/lili-player/bai-hat/hello-artist"),
        ).isEqualTo("hello-artist")
        assertThat(
            SongDeepLink.songPath("https://anhtuta.github.io/lili-player/#/bai-hat/hello-artist"),
        ).isEqualTo("hello-artist")
        assertThat(
            SongDeepLink.canonicalViewUri(
                "https://anhtuta.github.io/lili-player/#/bai-hat/hello-artist",
            ),
        ).isEqualTo("lilimusic://bai-hat/hello-artist")
    }

    @Test
    fun hashFragmentOnAnyHostMatchesWebRoute() {
        assertThat(SongDeepLink.songPath("https://example.com/#/bai-hat/alpha"))
            .isEqualTo("alpha")
    }

    @Test
    fun urlDecodesPath() {
        assertThat(SongDeepLink.songPath("lilimusic://bai-hat/c%C3%A0-ph%C3%AA"))
            .isEqualTo("cà-phê")
    }

    @Test
    fun missingSongIdIsRejected() {
        assertThat(SongDeepLink.songPath("lilimusic://bai-hat")).isNull()
        assertThat(SongDeepLink.songPath("https://blog.nenmongvietnam.com/bai-hat")).isNull()
        assertThat(SongDeepLink.songPath("https://blog.nenmongvietnam.com/bai-hat/")).isNull()
        assertThat(SongDeepLink.songPath("https://blog.nenmongvietnam.com/laravel/public")).isNull()
        assertThat(SongDeepLink.songPath("https://anhtuta.github.io/lili-player/")).isNull()
        assertThat(SongDeepLink.songPath("not a uri")).isNull()
    }
}
