package com.nenmongvietnam.lilimusic.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSong(
    val id: Long,
    val title: String,
    val artist: String? = null,
    val album: String? = null,
    val path: String,
    val type: String? = null,
    val listens: Int = 0,
    @SerialName("file_name") val fileName: String? = null,
    @SerialName("image_name") val imageName: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val lyric: String? = null,
    @SerialName("zing_id") val zingId: String? = null,
    @SerialName("song_of_the_year") val songOfTheYear: Int? = null,
    @SerialName("created_date") val createdDate: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val formatCreatedDate: String? = null,
)
