package com.anhtuta.lilimusic.feature.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhtuta.lilimusic.core.data.repository.DownloadRepository
import com.anhtuta.lilimusic.core.model.SongDownload
import com.anhtuta.lilimusic.core.playback.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DownloadsViewModel
    @Inject
    constructor(
        private val downloadRepository: DownloadRepository,
        private val playerController: PlayerController,
    ) : ViewModel() {
        private val confirmDeleteAll = MutableStateFlow(false)

        val uiState: StateFlow<DownloadsUiState> =
            combine(
                downloadRepository.observeDownloads(),
                confirmDeleteAll,
            ) { items, confirm ->
                DownloadsUiState(items = items, confirmDeleteAll = confirm)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DownloadsUiState(),
            )

        fun play(item: SongDownload) {
            if (!item.isComplete) return
            playerController.play(listOf(item.toSong()), item.songPath)
        }

        fun retry(item: SongDownload) {
            viewModelScope.launch { downloadRepository.retry(item.toSong()) }
        }

        fun remove(item: SongDownload) {
            viewModelScope.launch { downloadRepository.remove(item.songPath) }
        }

        fun onDeleteAllClick() {
            confirmDeleteAll.value = true
        }

        fun onCancelDeleteAll() {
            confirmDeleteAll.value = false
        }

        fun onConfirmDeleteAll() {
            confirmDeleteAll.value = false
            viewModelScope.launch { downloadRepository.removeAll() }
        }
    }
