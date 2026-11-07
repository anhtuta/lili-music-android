package com.anhtuta.lilimusic.core.data.repository

import app.cash.turbine.test
import com.anhtuta.lilimusic.core.data.testdoubles.FakeLyricCardPreferences
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DataStoreLyricCardSettingsRepositoryTest {
    @Test
    fun emitsDefaultsThenForwardsEachSetter() =
        runTest {
            val prefs = FakeLyricCardPreferences()
            val repository = DataStoreLyricCardSettingsRepository(prefs)

            repository.settings.test {
                assertThat(awaitItem()).isEqualTo(LyricCardSettings())

                repository.setTextAlign(LyricAlign.RIGHT)
                assertThat(awaitItem().textAlign).isEqualTo(LyricAlign.RIGHT)

                repository.setTextColor("#000000")
                assertThat(awaitItem().textColor).isEqualTo("#000000")

                repository.setBold(true)
                assertThat(awaitItem().isBold).isTrue()

                repository.setBackgroundColor("#1a1a2e")
                assertThat(awaitItem().backgroundColor).isEqualTo("#1a1a2e")

                repository.setUseSongImage(false)
                assertThat(awaitItem().useSongImage).isFalse()

                repository.setOverlayOpacity(25)
                assertThat(awaitItem().overlayOpacity).isEqualTo(25)

                repository.setBrightnessBoost(10)
                assertThat(awaitItem().brightnessBoost).isEqualTo(10)

                repository.setFontFamily(LyricCardFont.GEORGIA)
                assertThat(awaitItem().fontFamily).isEqualTo(LyricCardFont.GEORGIA)

                repository.setFontSize(18)
                assertThat(awaitItem().fontSize).isEqualTo(18)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
