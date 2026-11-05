package com.anhtuta.lilimusic.core.lyrics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricTimeDecoderTest {
    @Test
    fun decodesMinuteSecondHundredths() {
        assertThat(LyricTimeDecoder.decode("[02:17.88]")).isEqualTo(137_880L)
        assertThat(LyricTimeDecoder.decode("[00:00.00]")).isEqualTo(0L)
        assertThat(LyricTimeDecoder.decode("[00:12.36]")).isEqualTo(12_360L)
    }

    @Test
    fun decodesMillisecondPrecision() {
        assertThat(LyricTimeDecoder.decode("[00:00.463]")).isEqualTo(463L)
        assertThat(LyricTimeDecoder.decode("[00:01.5]")).isEqualTo(1_500L)
    }

    @Test
    fun decodesHourStamp() {
        assertThat(LyricTimeDecoder.decode("[1:03:45.32]")).isEqualTo(3_825_320L)
    }

    @Test
    fun invalidStampIsZero() {
        assertThat(LyricTimeDecoder.decode("")).isEqualTo(0L)
        assertThat(LyricTimeDecoder.decode("[00:01]")).isEqualTo(0L)
        assertThat(LyricTimeDecoder.decode("not a stamp")).isEqualTo(0L)
        assertThat(LyricTimeDecoder.decode("[mm:ss.xx]")).isEqualTo(0L)
    }
}
