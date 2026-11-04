package com.nenmongvietnam.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ImageUrlResolverTest {
    private val baseUrl = "https://blog.nenmongvietnam.com/laravel/public"

    @Test
    fun prefixesRelativePicturePath() {
        val resolved =
            ImageUrlResolver.resolve(
                imageUrl = "/api/song/picture?file=cover.jpg",
                baseUrl = baseUrl,
            )
        assertThat(resolved).isEqualTo("$baseUrl/api/song/picture?file=cover.jpg")
    }

    @Test
    fun prefixesRelativeAlbumPathWithoutLeadingSlash() {
        val resolved =
            ImageUrlResolver.resolve(
                imageUrl = "api/song/album?file=cover.jpg",
                baseUrl = "$baseUrl/",
            )
        assertThat(resolved).isEqualTo("$baseUrl/api/song/album?file=cover.jpg")
    }

    @Test
    fun leavesAbsoluteHttpsUrlUnchanged() {
        val absolute = "https://photo-resize-zmp3.zmdcdn.me/cover.jpg"
        assertThat(ImageUrlResolver.resolve(absolute, baseUrl)).isEqualTo(absolute)
    }

    @Test
    fun leavesAbsoluteHttpUrlUnchanged() {
        val absolute = "http://cdn.example/cover.jpg"
        assertThat(ImageUrlResolver.resolve(absolute, baseUrl)).isEqualTo(absolute)
    }

    @Test
    fun nullOrBlankBecomesNull() {
        assertThat(ImageUrlResolver.resolve(null, baseUrl)).isNull()
        assertThat(ImageUrlResolver.resolve("  ", baseUrl)).isNull()
        assertThat(ImageUrlResolver.resolve("", baseUrl)).isNull()
    }
}
