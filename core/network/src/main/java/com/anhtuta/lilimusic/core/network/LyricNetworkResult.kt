package com.anhtuta.lilimusic.core.network

sealed class LyricNetworkResult {
    data class Body(
        val text: String,
    ) : LyricNetworkResult()

    data object Missing : LyricNetworkResult()
}
