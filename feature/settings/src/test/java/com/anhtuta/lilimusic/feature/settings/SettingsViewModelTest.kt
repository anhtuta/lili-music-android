package com.anhtuta.lilimusic.feature.settings

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.playback.FakePlayerController
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadsPlanDefaults() =
        runTest {
            val viewModel = SettingsViewModel(FakeSettingsRepository(), FakePlayerController())

            viewModel.uiState.test {
                val state = awaitItemUntil { it.settings == PlayerSettings() && it.speed == 1.0f }
                assertThat(state.settings.lyricTheme).isEqualTo(LyricTheme.PICTURE_BG)
                assertThat(state.settings.karaokeMode).isTrue()
                assertThat(state.settings.giganticLine).isFalse()
                assertThat(state.settings.translate).isFalse()
                assertThat(state.settings.appTheme).isEqualTo(AppTheme.LIGHT)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun settersUpdatePersistedSettings() =
        runTest {
            val repository = FakeSettingsRepository()
            val viewModel = SettingsViewModel(repository, FakePlayerController())

            viewModel.setLyricAlign(LyricAlign.LEFT)
            viewModel.setLyricTheme(LyricTheme.DARK)
            viewModel.setKaraokeMode(false)
            viewModel.setBlurMode(true)
            viewModel.setWordAppear(true)
            viewModel.setLineAppear(true)
            viewModel.setGiganticLine(true)
            viewModel.setTranslate(true)
            viewModel.setAppTheme(AppTheme.DARK)

            viewModel.uiState.test {
                val state =
                    awaitItemUntil {
                        it.settings.lyricAlign == LyricAlign.LEFT &&
                            it.settings.translate &&
                            it.settings.appTheme == AppTheme.DARK
                    }
                assertThat(state.settings.lyricTheme).isEqualTo(LyricTheme.DARK)
                assertThat(state.settings.karaokeMode).isFalse()
                assertThat(state.settings.blurMode).isTrue()
                assertThat(state.settings.wordAppear).isTrue()
                assertThat(state.settings.lineAppear).isTrue()
                assertThat(state.settings.giganticLine).isTrue()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun speedStepsGoThroughPlayerController() =
        runTest {
            val controller = FakePlayerController()
            val viewModel = SettingsViewModel(FakeSettingsRepository(), controller)

            viewModel.increaseSpeed()
            viewModel.uiState.test {
                val faster = awaitItemUntil { it.speed == 1.1f }
                assertThat(faster.speed).isEqualTo(1.1f)
                assertThat(controller.state.value.speed).isEqualTo(1.1f)
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.decreaseSpeed()
            viewModel.decreaseSpeed()
            viewModel.uiState.test {
                val slower = awaitItemUntil { it.speed == 0.9f }
                assertThat(slower.speed).isEqualTo(0.9f)
                cancelAndIgnoreRemainingEvents()
            }
        }
}

private suspend fun ReceiveTurbine<SettingsUiState>.awaitItemUntil(predicate: (SettingsUiState) -> Boolean): SettingsUiState {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}
