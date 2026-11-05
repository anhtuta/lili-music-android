package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricFormatTest {
    @Test
    fun fromFileNameMapsExtensions() {
        assertThat(LyricFormat.fromFileName("song.lrc")).isEqualTo(LyricFormat.LRC)
        assertThat(LyricFormat.fromFileName("Song.XLRC")).isEqualTo(LyricFormat.XLRC)
        assertThat(LyricFormat.fromFileName("song.trc")).isEqualTo(LyricFormat.TRC)
        assertThat(LyricFormat.fromFileName("song.xtrc")).isEqualTo(LyricFormat.TRC)
        assertThat(LyricFormat.fromFileName("no-ext")).isEqualTo(LyricFormat.TRC)
    }

    @Test
    fun missingBodyMatchesWebSentinelAndBlank() {
        assertThat(LyricFormat.isMissingBody("Lyric doesn't exist!")).isTrue()
        assertThat(LyricFormat.isMissingBody("  Lyric doesn't exist!  ")).isTrue()
        assertThat(LyricFormat.isMissingBody("")).isTrue()
        assertThat(LyricFormat.isMissingBody("   ")).isTrue()
        assertThat(LyricFormat.isMissingBody("[00:01.00]hello")).isFalse()
    }
}
