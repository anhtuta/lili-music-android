package com.anhtuta.lilimusic.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhtuta.lilimusic.core.data.network.NetworkMonitor
import com.anhtuta.lilimusic.core.data.repository.DownloadRepository
import com.anhtuta.lilimusic.core.data.repository.SongRepository
import com.anhtuta.lilimusic.core.datastore.PlaybackPreferences
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.OfflinePlaybackPolicy
import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongDownload
import com.anhtuta.lilimusic.core.playback.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerViewModel
    @Inject
    constructor(
        private val playerController: PlayerController,
        private val songRepository: SongRepository,
        private val playbackPreferences: PlaybackPreferences,
        private val downloadRepository: DownloadRepository,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModel() {
        val uiState: StateFlow<PlayerUiState> =
            combine(
                playerController.state,
                downloadRepository.observeDownloads(),
            ) { playback, downloads ->
                val download = playback.currentSong?.let { song -> downloads.find { it.songPath == song.path } }
                playback.toPlayerUiState().copy(
                    song =
                        playback.currentSong?.let { song ->
                            OfflinePlaybackPolicy.withDisplayImage(song, download?.coverLocalUri)
                        },
                    downloadStatus = download?.status ?: DownloadStatus.NONE,
                    downloadProgress = download?.progressPercent ?: 0,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlayerUiState(),
            )

        init {
            viewModelScope.launch { restoreLastPlayed() }
        }

        fun ensurePlaying(path: String) {
            viewModelScope.launch {
                if (playerController.state.value.currentSong
                        ?.path == path
                ) {
                    return@launch
                }
                val songs = songRepository.observePlayableSongs().first()
                val downloads = downloadRepository.observeDownloads().first()
                val online = networkMonitor.isOnline.first()
                val status = downloads.find { it.songPath == path }?.status ?: DownloadStatus.NONE
                if (!songs.any { it.path == path }) return@launch
                if (!OfflinePlaybackPolicy.isPlayable(online, status)) return@launch
                playerController.play(songs.withLocalCovers(downloads, online), path)
            }
        }

        fun playPause() {
            playerController.playPause()
        }

        fun skipToNext() {
            playerController.skipToNext()
        }

        fun skipToPrevious() {
            playerController.skipToPrevious()
        }

        fun seekTo(positionMs: Long) {
            playerController.seekTo(positionMs)
        }

        fun seekBack() {
            playerController.seekBack()
        }

        fun seekForward() {
            playerController.seekForward()
        }

        fun toggleShuffle() {
            playerController.setPlayMode(
                playerController.state.value.playMode
                    .toggleShuffle(),
            )
        }

        fun toggleRepeatOne() {
            playerController.setPlayMode(
                playerController.state.value.playMode
                    .toggleRepeatOne(),
            )
        }

        fun increaseSpeed() {
            playerController.setSpeed(PlaybackSpeed.increase(playerController.state.value.speed))
        }

        fun decreaseSpeed() {
            playerController.setSpeed(PlaybackSpeed.decrease(playerController.state.value.speed))
        }

        fun onDownloadAction() {
            val song = playerController.state.value.currentSong ?: return
            viewModelScope.launch {
                when (uiState.value.downloadStatus) {
                    DownloadStatus.NONE, DownloadStatus.FAILED -> downloadRepository.download(song)
                    DownloadStatus.COMPLETED -> downloadRepository.remove(song.path)
                    DownloadStatus.QUEUED, DownloadStatus.DOWNLOADING -> Unit
                }
            }
        }

        private suspend fun restoreLastPlayed() {
            if (playerController.state.value.currentSong != null) return
            val prefs = playbackPreferences.preferences.first()
            val path = prefs.lastSongPath ?: return
            val songs = songRepository.observePlayableSongs().first { catalog -> catalog.any { it.path == path } }
            if (playerController.state.value.currentSong != null) return
            val downloads = downloadRepository.observeDownloads().first()
            val online = networkMonitor.isOnline.first()
            val status = downloads.find { it.songPath == path }?.status ?: DownloadStatus.NONE
            if (!OfflinePlaybackPolicy.isPlayable(online, status)) return
            playerController.setPlayMode(prefs.playMode)
            playerController.setSpeed(prefs.speed)
            playerController.prepare(songs.withLocalCovers(downloads, online), path)
        }

        private fun List<Song>.withLocalCovers(
            downloads: List<SongDownload>,
            isOnline: Boolean,
        ): List<Song> {
            val byPath = downloads.associateBy { it.songPath }
            val completed = downloads.filter { it.isComplete }.map { it.songPath }.toSet()
            return OfflinePlaybackPolicy.playQueue(this, isOnline, completed).map { song ->
                OfflinePlaybackPolicy.withDisplayImage(song, byPath[song.path]?.coverLocalUri)
            }
        }
    }
