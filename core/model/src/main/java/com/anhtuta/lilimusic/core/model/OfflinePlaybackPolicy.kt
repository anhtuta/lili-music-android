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

    /**
     * Online: the full playable catalog in API order. Offline: that catalog filtered to completed downloads.
     * Search must not pass the visible/filtered list.
     */
    fun playQueue(
        catalog: List<Song>,
        isOnline: Boolean,
        completedPaths: Set<String>,
    ): List<Song> = if (isOnline) catalog else catalog.filter { it.path in completedPaths }

    fun displayImageUrl(
        remoteImageUrl: String?,
        localCoverUri: String?,
    ): String? = localCoverUri ?: remoteImageUrl

    fun withDisplayImage(
        song: Song,
        localCoverUri: String?,
    ): Song = song.copy(imageUrl = displayImageUrl(song.imageUrl, localCoverUri))
}
