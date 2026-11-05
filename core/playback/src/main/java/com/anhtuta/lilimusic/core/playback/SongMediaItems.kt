package com.anhtuta.lilimusic.core.playback

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.anhtuta.lilimusic.core.model.Song

object SongMediaItems {
    fun from(song: Song): MediaItem =
        MediaItem
            .Builder()
            .setMediaId(song.path)
            .setUri(song.streamUrl)
            .setMediaMetadata(
                MediaMetadata
                    .Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .setArtworkUri(song.imageUrl?.toUri())
                    .build(),
            ).build()

    fun from(songs: List<Song>): List<MediaItem> = songs.map(::from)
}
