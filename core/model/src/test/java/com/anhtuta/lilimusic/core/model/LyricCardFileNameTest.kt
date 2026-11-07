package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricCardFileNameTest {
    @Test
    fun stripsWhitespaceLikeWebReplaceAll() {
        assertThat(LyricCardFileName.jpeg("Hello World", "Tú Anh"))
            .isEqualTo("lyric-card-HelloWorld-TúAnh.jpg")
    }

    @Test
    fun missingTitleOrArtistUseWebFallbacks() {
        assertThat(LyricCardFileName.jpeg(null, null))
            .isEqualTo("lyric-card-untitled-unknown.jpg")
        assertThat(LyricCardFileName.jpeg("   ", "Artist"))
            .isEqualTo("lyric-card-untitled-Artist.jpg")
    }
}
