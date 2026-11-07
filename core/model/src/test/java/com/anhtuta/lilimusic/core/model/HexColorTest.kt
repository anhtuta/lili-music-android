package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HexColorTest {
    @Test
    fun normalizesHashAndCase() {
        assertThat(HexColor.normalize("667EEA", "#ffffff")).isEqualTo("#667eea")
        assertThat(HexColor.normalize("#667EEA", "#ffffff")).isEqualTo("#667eea")
    }

    @Test
    fun invalidValuesUseDefault() {
        assertThat(HexColor.normalize(null, "#ffffff")).isEqualTo("#ffffff")
        assertThat(HexColor.normalize("", "#667eea")).isEqualTo("#667eea")
        assertThat(HexColor.normalize("#fff", "#667eea")).isEqualTo("#667eea")
        assertThat(HexColor.normalize("not-a-color", "#000000")).isEqualTo("#000000")
    }

    @Test
    fun rgbSplitsChannels() {
        val rgb = HexColor.rgb("#e94560", "#000000")
        assertThat(HexColor.red(rgb)).isEqualTo(0xE9)
        assertThat(HexColor.green(rgb)).isEqualTo(0x45)
        assertThat(HexColor.blue(rgb)).isEqualTo(0x60)
    }
}
