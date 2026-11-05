package com.anhtuta.lilimusic.feature.player

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlayerTimeFormatterTest {
    @Test
    fun formatsMinutesAndSeconds() {
        assertThat(PlayerTimeFormatter.format(0)).isEqualTo("0:00")
        assertThat(PlayerTimeFormatter.format(15_000)).isEqualTo("0:15")
        assertThat(PlayerTimeFormatter.format(60_000)).isEqualTo("1:00")
        assertThat(PlayerTimeFormatter.format(125_000)).isEqualTo("2:05")
    }
}
