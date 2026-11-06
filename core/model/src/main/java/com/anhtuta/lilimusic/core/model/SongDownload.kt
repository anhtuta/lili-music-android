package com.anhtuta.lilimusic.core.model

data class SongDownload(
    val songPath: String,
    val songId: Long,
    val title: String,
    val artist: String,
    val streamUrl: String,
    val fileName: String,
    val imageUrl: String?,
    val lyricFileName: String?,
    val coverLocalUri: String?,
    val status: DownloadStatus,
    val progressPercent: Int,
    val bytesDownloaded: Long,
    val errorMessage: String?,
    val updatedAtEpochMs: Long,
) {
    val displayImageUrl: String?
        get() = OfflinePlaybackPolicy.displayImageUrl(imageUrl, coverLocalUri)

    val isComplete: Boolean
        get() = status == DownloadStatus.COMPLETED

    val isActive: Boolean
        get() = status.isActive

    fun toSong(): Song =
        Song(
            id = songId,
            title = title,
            artist = artist,
            album = null,
            path = songPath,
            type = null,
            listens = 0,
            fileName = fileName,
            imageName = null,
            imageUrl = displayImageUrl,
            lyric = lyricFileName?.let(::LyricRef),
            zingId = null,
            songOfTheYear = null,
            createdDate = null,
            formatCreatedDate = null,
            streamUrl = streamUrl,
        )

    companion object {
        fun queued(
            song: Song,
            updatedAtEpochMs: Long,
        ): SongDownload =
            SongDownload(
                songPath = song.path,
                songId = song.id,
                title = song.title,
                artist = song.artist,
                streamUrl = song.streamUrl,
                fileName = song.fileName,
                imageUrl = song.imageUrl,
                lyricFileName = song.lyric?.fileName,
                coverLocalUri = null,
                status = DownloadStatus.QUEUED,
                progressPercent = 0,
                bytesDownloaded = 0L,
                errorMessage = null,
                updatedAtEpochMs = updatedAtEpochMs,
            )
    }
}
