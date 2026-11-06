package com.anhtuta.lilimusic.feature.library

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SongOfTheYearPaletteTest {
    @Test
    fun uniqueYearsAreDistinctAndDescending() {
        assertThat(SongOfTheYearPalette.uniqueYearsDescending(listOf(2018, null, 2024, 2018, 2020)))
            .containsExactly(2024, 2020, 2018)
            .inOrder()
    }

    @Test
    fun highestYearUsesFirstRainbowColorAndDarkText() {
        val unique = listOf(2024, 2020, 2018)
        assertThat(SongOfTheYearPalette.backgroundArgb(2024, unique))
            .isEqualTo(SongOfTheYearPalette.backgrounds[0])
        assertThat(SongOfTheYearPalette.contentArgb(2024, unique))
            .isEqualTo(SongOfTheYearPalette.DARK_TEXT)
    }

    @Test
    fun laterYearsUseLightText() {
        val unique = (0 until 12).map { 2024 - it }
        assertThat(SongOfTheYearPalette.contentArgb(2016, unique))
            .isEqualTo(SongOfTheYearPalette.LIGHT_TEXT)
    }
}
