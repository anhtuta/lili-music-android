package com.anhtuta.lilimusic.core.model

object OfflinePlaybackPolicy {
    fun isPlayable(
        isOnline: Boolean,
        status: DownloadStatus,
    ): Boolean = isOnline || status == DownloadStatus.COMPLETED

    fun isUnavailableOffline(
        isOnline: Boolean,
        status: DownloadStatus,
    ): Boolean = !isOnline && status != DownloadStatus.COMPLETED

    fun playQueue(
        songs: List<Song>,
        isOnline: Boolean,
        completedPaths: Set<String>,
    ): List<Song> = if (isOnline) songs else songs.filter { it.path in completedPaths }

    fun displayImageUrl(
        remoteImageUrl: String?,
        localCoverUri: String?,
    ): String? = localCoverUri ?: remoteImageUrl

    fun withDisplayImage(
        song: Song,
        localCoverUri: String?,
    ): Song = song.copy(imageUrl = displayImageUrl(song.imageUrl, localCoverUri))
}
