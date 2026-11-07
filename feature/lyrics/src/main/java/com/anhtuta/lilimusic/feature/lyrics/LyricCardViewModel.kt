package com.anhtuta.lilimusic.feature.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhtuta.lilimusic.core.data.di.IoDispatcher
import com.anhtuta.lilimusic.core.data.repository.LyricCardSettingsRepository
import com.anhtuta.lilimusic.core.data.repository.LyricLoadResult
import com.anhtuta.lilimusic.core.data.repository.LyricRepository
import com.anhtuta.lilimusic.core.data.repository.SettingsRepository
import com.anhtuta.lilimusic.core.lyrics.LyricCardText
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFileName
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import com.anhtuta.lilimusic.core.model.ParsedLyric
import com.anhtuta.lilimusic.core.playback.PlaybackState
import com.anhtuta.lilimusic.core.playback.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class LyricCardViewModel
    @Inject
    constructor(
        private val playerController: PlayerController,
        private val lyricRepository: LyricRepository,
        private val settingsRepository: SettingsRepository,
        private val cardSettingsRepository: LyricCardSettingsRepository,
        private val coverLoader: LyricCardCoverLoader,
        private val files: LyricCardFiles,
        private val clipboard: LyricCardClipboard,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val loadResult = MutableStateFlow<LyricLoadResult?>(null)
        private val step = MutableStateFlow(LyricCardStep.HIDDEN)
        private val selectedIds = MutableStateFlow<Set<Int>>(emptySet())
        private val showNoLyricWarning = MutableStateFlow(false)
        private val shareUri = MutableStateFlow<android.net.Uri?>(null)
        private val shareFileName = MutableStateFlow<String?>(null)
        private val copied = MutableStateFlow(false)
        private val shareFailed = MutableStateFlow(false)

        val uiState: StateFlow<LyricCardUiState> =
            combine(
                combine(
                    playerController.state,
                    loadResult,
                    cardSettingsRepository.settings,
                    settingsRepository.settings,
                    step,
                ) { playback, load, card, settings, currentStep ->
                    CardSnapshot(playback, load, card, settings.translate, currentStep)
                },
                selectedIds,
                combine(showNoLyricWarning, shareUri, copied, shareFailed, shareFileName) {
                    warning,
                    uri,
                    didCopy,
                    failed,
                    fileName,
                    ->
                    TransientFlags(warning, uri, didCopy, failed, fileName)
                },
            ) { snapshot, ids, flags ->
                toUiState(snapshot, ids, flags)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LyricCardUiState(),
            )

        init {
            viewModelScope.launch {
                playerController.state
                    .map { playback ->
                        playback.currentSong?.path to playback.currentSong?.lyric?.fileName
                    }.distinctUntilChanged()
                    .collect { (path, fileName) ->
                        if (path == null) {
                            loadResult.value = null
                            close()
                        } else {
                            loadResult.value = lyricRepository.loadLyric(fileName)
                        }
                    }
            }
        }

        fun open() {
            val lyric = parsedLyric()
            if (!LyricCardText.canCreate(lyric)) {
                showNoLyricWarning.value = true
                return
            }
            selectedIds.value = emptySet()
            shareUri.value = null
            shareFailed.value = false
            copied.value = false
            step.value = LyricCardStep.SELECT
        }

        fun close() {
            step.value = LyricCardStep.HIDDEN
            selectedIds.value = emptySet()
            shareUri.value = null
            shareFileName.value = null
        }

        fun consumeNoLyricWarning() {
            showNoLyricWarning.value = false
        }

        fun toggleLine(id: Int) {
            selectedIds.update { current ->
                if (id in current) current - id else current + id
            }
        }

        fun selectAll() {
            selectedIds.value =
                uiState.value.lines
                    .map { it.id }
                    .toSet()
        }

        fun generate() {
            if (uiState.value.selectedText.isBlank()) {
                return
            }
            step.value = LyricCardStep.CONFIGURE
        }

        fun backToSelection() {
            shareUri.value = null
            step.value = LyricCardStep.SELECT
        }

        fun setTextAlign(align: LyricAlign) {
            viewModelScope.launch { cardSettingsRepository.setTextAlign(align) }
        }

        fun setTextColor(color: String) {
            viewModelScope.launch { cardSettingsRepository.setTextColor(color) }
        }

        fun setBold(enabled: Boolean) {
            viewModelScope.launch { cardSettingsRepository.setBold(enabled) }
        }

        fun setBackgroundColor(color: String) {
            viewModelScope.launch { cardSettingsRepository.setBackgroundColor(color) }
        }

        fun setUseSongImage(enabled: Boolean) {
            viewModelScope.launch { cardSettingsRepository.setUseSongImage(enabled) }
        }

        fun setOverlayOpacity(percent: Int) {
            viewModelScope.launch { cardSettingsRepository.setOverlayOpacity(percent) }
        }

        fun setBrightnessBoost(percent: Int) {
            viewModelScope.launch { cardSettingsRepository.setBrightnessBoost(percent) }
        }

        fun setFontFamily(font: LyricCardFont) {
            viewModelScope.launch { cardSettingsRepository.setFontFamily(font) }
        }

        fun setFontSize(size: Int) {
            viewModelScope.launch { cardSettingsRepository.setFontSize(size) }
        }

        fun copySelectedText() {
            val text = uiState.value.selectedText
            if (text.isBlank()) {
                return
            }
            clipboard.copy(text)
            copied.value = true
        }

        fun consumeCopied() {
            copied.value = false
        }

        fun consumeShare() {
            shareUri.value = null
            shareFileName.value = null
        }

        fun consumeShareFailed() {
            shareFailed.value = false
        }

        fun shareCard() {
            val state = uiState.value
            if (state.selectedText.isBlank()) {
                return
            }
            viewModelScope.launch {
                runCatching {
                    val cover =
                        if (state.config.useSongImage) {
                            withContext(ioDispatcher) { coverLoader.load(state.coverUrl) }
                        } else {
                            null
                        }
                    val bitmap =
                        withContext(ioDispatcher) {
                            LyricCardRenderer.render(
                                LyricCardRenderRequest(
                                    settings = state.config,
                                    selectedText = state.selectedText,
                                    title = state.songTitle,
                                    artist = state.songArtist,
                                    cover = cover,
                                ),
                            )
                        }
                    val fileName = LyricCardFileName.jpeg(state.songTitle, state.songArtist)
                    val jpeg = withContext(ioDispatcher) { LyricCardRenderer.toJpeg(bitmap) }
                    files.writeJpeg(jpeg, fileName) to fileName
                }.onSuccess { (uri, fileName) ->
                    shareUri.value = uri
                    shareFileName.value = fileName
                    shareFailed.value = false
                }.onFailure {
                    shareFailed.value = true
                }
            }
        }

        private fun parsedLyric(): ParsedLyric? = (loadResult.value as? LyricLoadResult.Ready)?.lyric

        private fun toUiState(
            snapshot: CardSnapshot,
            selected: Set<Int>,
            flags: TransientFlags,
        ): LyricCardUiState {
            val song = snapshot.playback.currentSong
            val lyric = (snapshot.load as? LyricLoadResult.Ready)?.lyric
            val sourceLines =
                if (lyric != null && LyricCardText.canCreate(lyric)) {
                    LyricCardText.selectableLines(lyric, snapshot.translate)
                } else {
                    emptyList()
                }
            val lines =
                sourceLines.mapIndexed { index, text ->
                    LyricCardSelectableLine(
                        id = index,
                        text = text,
                        selected = index in selected,
                    )
                }
            return LyricCardUiState(
                step = snapshot.step,
                showNoLyricWarning = flags.showNoLyricWarning,
                songTitle = song?.title.orEmpty(),
                songArtist = song?.artist.orEmpty(),
                coverUrl = song?.imageUrl,
                lines = lines,
                selectedText = lines.filter { it.selected }.joinToString("\n") { it.text },
                config = snapshot.config,
                shareUri = flags.shareUri,
                shareFileName = flags.shareFileName,
                copied = flags.copied,
                shareFailed = flags.shareFailed,
            )
        }

        private data class CardSnapshot(
            val playback: PlaybackState,
            val load: LyricLoadResult?,
            val config: LyricCardSettings,
            val translate: Boolean,
            val step: LyricCardStep,
        )

        private data class TransientFlags(
            val showNoLyricWarning: Boolean,
            val shareUri: android.net.Uri?,
            val copied: Boolean,
            val shareFailed: Boolean,
            val shareFileName: String?,
        )
    }
