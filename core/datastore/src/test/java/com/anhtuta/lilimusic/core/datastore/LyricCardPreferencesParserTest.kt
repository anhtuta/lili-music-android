package com.anhtuta.lilimusic.core.datastore

import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricCardPreferencesParserTest {
    @Test
    fun missingValuesUseWebDefaults() {
        assertThat(LyricCardPreferencesParser.parse(LyricCardPreferenceSnapshot()))
            .isEqualTo(LyricCardSettings())
    }

    @Test
    fun unknownAndOutOfRangeValuesAreClamped() {
        val parsed =
            LyricCardPreferencesParser.parse(
                LyricCardPreferenceSnapshot(
                    textAlign = "justify",
                    textColor = "red",
                    backgroundColor = "#fff",
                    useSongImage = false,
                    overlayOpacity = 99,
                    brightnessBoost = -4,
                    fontFamily = "Comic Sans",
                    fontSize = 2,
                    isBold = true,
                ),
            )
        assertThat(parsed.textAlign).isEqualTo(LyricAlign.LEFT)
        assertThat(parsed.textColor).isEqualTo(LyricCardSettings.DEFAULT_TEXT_COLOR)
        assertThat(parsed.backgroundColor).isEqualTo(LyricCardSettings.DEFAULT_BACKGROUND)
        assertThat(parsed.useSongImage).isFalse()
        assertThat(parsed.overlayOpacity).isEqualTo(80)
        assertThat(parsed.brightnessBoost).isEqualTo(0)
        assertThat(parsed.fontFamily).isEqualTo(LyricCardFont.OPEN_SANS)
        assertThat(parsed.fontSize).isEqualTo(5)
        assertThat(parsed.isBold).isTrue()
    }

    @Test
    fun parsesStoredWebValues() {
        val parsed =
            LyricCardPreferencesParser.parse(
                LyricCardPreferenceSnapshot(
                    textAlign = "right",
                    textColor = "#000000",
                    isBold = true,
                    backgroundColor = "#1a1a2e",
                    useSongImage = false,
                    overlayOpacity = 40,
                    brightnessBoost = 15,
                    fontFamily = "Georgia",
                    fontSize = 20,
                ),
            )
        assertThat(parsed).isEqualTo(
            LyricCardSettings(
                textAlign = LyricAlign.RIGHT,
                textColor = "#000000",
                isBold = true,
                backgroundColor = "#1a1a2e",
                useSongImage = false,
                overlayOpacity = 40,
                brightnessBoost = 15,
                fontFamily = LyricCardFont.GEORGIA,
                fontSize = 20,
            ),
        )
    }
}
