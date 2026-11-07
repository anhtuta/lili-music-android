package com.anhtuta.lilimusic.feature.lyrics

import android.graphics.Bitmap
import android.net.Uri
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.data.repository.LyricCardSettingsRepository
import com.anhtuta.lilimusic.core.data.repository.LyricLoadResult
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import com.anhtuta.lilimusic.core.playback.FakePlayerController
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class LyricCardViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dispatcher = UnconfinedTestDispatcher()

    @Test
    fun missingLyricSetsWarningAndStaysHidden() =
        runTest {
            val viewModel = viewModel(load = LyricLoadResult.Missing)
            viewModel.uiState.test {
                awaitItemUntil { it.songTitle == "Alpha" }
                viewModel.open()
                val warned = awaitItemUntil { it.showNoLyricWarning }
                assertThat(warned.step).isEqualTo(LyricCardStep.HIDDEN)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun toggleLinesBuildsSelectedTextThenGenerateOpensConfigure() =
        runTest {
            val viewModel = viewModel(load = LyricLoadResult.Ready(sampleParsedLyric()))
            viewModel.uiState.test {
                awaitItemUntil { it.songTitle == "Alpha" }
                viewModel.open()
                val opened = awaitItemUntil { it.step == LyricCardStep.SELECT && it.lines.isNotEmpty() }
                viewModel.toggleLine(opened.lines.first().id)
                val selected = awaitItemUntil { it.selectedText.isNotBlank() }
                assertThat(selected.canGenerate).isTrue()
                viewModel.generate()
                val configure = awaitItemUntil { it.step == LyricCardStep.CONFIGURE }
                assertThat(configure.selectedText).isEqualTo(selected.selectedText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun copyAndShareUseExportedTextAndJpeg() =
        runTest {
            val clipboard = RecordingClipboard()
            val files = RecordingFiles()
            val viewModel =
                viewModel(
                    load = LyricLoadResult.Ready(sampleParsedLyric()),
                    clipboard = clipboard,
                    files = files,
                )
            viewModel.uiState.test {
                awaitItemUntil { it.songTitle == "Alpha" }
                viewModel.open()
                val opened = awaitItemUntil { it.lines.isNotEmpty() }
                viewModel.selectAll()
                awaitItemUntil { it.selectedText.contains("First") }
                viewModel.copySelectedText()
                awaitItemUntil { it.copied }
                assertThat(clipboard.copied).contains("First")
                viewModel.generate()
                awaitItemUntil { it.step == LyricCardStep.CONFIGURE }
                viewModel.shareCard()
                val shared = awaitItemUntil { it.shareUri != null }
                assertThat(files.fileName).isEqualTo("lyric-card-Alpha-Artist.jpg")
                assertThat(files.bytes!!.size).isGreaterThan(32)
                assertThat(shared.shareFileName).isEqualTo("lyric-card-Alpha-Artist.jpg")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun configSettersForwardToRepository() =
        runTest {
            val cards = FakeLyricCardSettingsRepository()
            val viewModel =
                viewModel(
                    load = LyricLoadResult.Ready(sampleParsedLyric()),
                    cards = cards,
                )
            viewModel.setTextAlign(LyricAlign.RIGHT)
            viewModel.setBold(true)
            viewModel.setFontFamily(LyricCardFont.GEORGIA)
            viewModel.setFontSize(20)
            viewModel.setUseSongImage(false)
            viewModel.uiState.test {
                val state =
                    awaitItemUntil {
                        it.config.textAlign == LyricAlign.RIGHT &&
                            it.config.isBold &&
                            it.config.fontFamily == LyricCardFont.GEORGIA &&
                            it.config.fontSize == 20 &&
                            !it.config.useSongImage
                    }
                assertThat(state.config.fontFamily).isEqualTo(LyricCardFont.GEORGIA)
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun viewModel(
        load: LyricLoadResult,
        cards: FakeLyricCardSettingsRepository = FakeLyricCardSettingsRepository(),
        clipboard: RecordingClipboard = RecordingClipboard(),
        files: RecordingFiles = RecordingFiles(),
    ): LyricCardViewModel {
        val controller = FakePlayerController()
        controller.play(listOf(lyricsTestSong()), "alpha")
        return LyricCardViewModel(
            playerController = controller,
            lyricRepository = FakeLyricRepository(load),
            settingsRepository = FakeSettingsRepository(),
            cardSettingsRepository = cards,
            coverLoader = FakeCoverLoader(),
            files = files,
            clipboard = clipboard,
            ioDispatcher = dispatcher,
        )
    }
}

private class FakeCoverLoader : LyricCardCoverLoader {
    override suspend fun load(url: String?): Bitmap? = null
}

private class RecordingClipboard : LyricCardClipboard {
    var copied: String? = null

    override fun copy(text: String) {
        copied = text
    }
}

private class RecordingFiles : LyricCardFiles {
    var bytes: ByteArray? = null
    var fileName: String? = null

    override fun writeJpeg(
        bytes: ByteArray,
        fileName: String,
    ): Uri {
        this.bytes = bytes
        this.fileName = fileName
        return Uri.parse("content://com.anhtuta.lilimusic.fileprovider/lyric-cards/$fileName")
    }
}

private class FakeLyricCardSettingsRepository(
    initial: LyricCardSettings = LyricCardSettings(),
) : LyricCardSettingsRepository {
    private val state = MutableStateFlow(initial)
    override val settings: Flow<LyricCardSettings> = state

    override suspend fun setTextAlign(align: LyricAlign) {
        state.value = state.value.copy(textAlign = align)
    }

    override suspend fun setTextColor(color: String) {
        state.value = state.value.copy(textColor = color)
    }

    override suspend fun setBold(enabled: Boolean) {
        state.value = state.value.copy(isBold = enabled)
    }

    override suspend fun setBackgroundColor(color: String) {
        state.value = state.value.copy(backgroundColor = color)
    }

    override suspend fun setUseSongImage(enabled: Boolean) {
        state.value = state.value.copy(useSongImage = enabled)
    }

    override suspend fun setOverlayOpacity(percent: Int) {
        state.value = state.value.copy(overlayOpacity = percent)
    }

    override suspend fun setBrightnessBoost(percent: Int) {
        state.value = state.value.copy(brightnessBoost = percent)
    }

    override suspend fun setFontFamily(font: LyricCardFont) {
        state.value = state.value.copy(fontFamily = font)
    }

    override suspend fun setFontSize(size: Int) {
        state.value = state.value.copy(fontSize = size)
    }
}

private suspend fun app.cash.turbine.ReceiveTurbine<LyricCardUiState>.awaitItemUntil(
    predicate: (LyricCardUiState) -> Boolean,
): LyricCardUiState {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}
