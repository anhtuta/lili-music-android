package com.anhtuta.lilimusic.core.data.repository

import app.cash.turbine.test
import com.anhtuta.lilimusic.core.data.testdoubles.FakeSettingsPreferences
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricFontSize
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DataStoreSettingsRepositoryTest {
    @Test
    fun emitsDefaultsThenForwardsEachSetter() =
        runTest {
            val prefs = FakeSettingsPreferences()
            val repository = DataStoreSettingsRepository(prefs)

            repository.settings.test {
                assertThat(awaitItem()).isEqualTo(PlayerSettings())

                repository.setLyricAlign(LyricAlign.RIGHT)
                assertThat(awaitItem().lyricAlign).isEqualTo(LyricAlign.RIGHT)

                repository.setLyricTheme(LyricTheme.LIGHT)
                assertThat(awaitItem().lyricTheme).isEqualTo(LyricTheme.LIGHT)

                repository.setKaraokeMode(false)
                assertThat(awaitItem().karaokeMode).isFalse()

                repository.setBlurMode(true)
                assertThat(awaitItem().blurMode).isTrue()

                repository.setGiganticLine(false)
                assertThat(awaitItem().giganticLine).isFalse()

                repository.setWordAppear(true)
                assertThat(awaitItem().wordAppear).isTrue()

                repository.setLineAppear(true)
                assertThat(awaitItem().lineAppear).isTrue()

                repository.setTranslate(true)
                assertThat(awaitItem().translate).isTrue()

                repository.setAppTheme(AppTheme.DARK)
                assertThat(awaitItem().appTheme).isEqualTo(AppTheme.DARK)

                repository.setLyricFontSize(LyricFontSize.LARGE)
                assertThat(awaitItem().lyricFontSize).isEqualTo(LyricFontSize.LARGE)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
