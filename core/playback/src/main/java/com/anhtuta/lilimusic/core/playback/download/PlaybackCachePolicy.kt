@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback.download

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource

object PlaybackCachePolicy {
    const val CACHE_DIR = "media-downloads"
    const val WRITE_DURING_PLAYBACK = false

    fun flags(): Int = CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
}
