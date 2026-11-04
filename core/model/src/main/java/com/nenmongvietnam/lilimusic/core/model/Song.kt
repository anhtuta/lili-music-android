package com.nenmongvietnam.lilimusic.core.model

/**
 * Playable catalog song. [fileName] is always non-blank; Zing-only rows are dropped before this type is created.
 */
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String?,
    val path: String,
    val type: String?,
    val listens: Int,
    val fileName: String,
    val imageName: String?,
    val imageUrl: String?,
    val lyric: LyricRef?,
    val zingId: String?,
    val songOfTheYear: Int?,
    val createdDate: String?,
    val formatCreatedDate: String?,
    val streamUrl: String,
) {
    init {
        require(fileName.isNotBlank()) {
            "Song.fileName must be non-blank; hide Zing-only tracks before creating Song"
        }
    }
}
