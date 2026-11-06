@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback.download

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaybackCachePolicyTest {
    @Test
    fun playbackReadsDownloadCacheWithoutWritingIt() {
        assertThat(PlaybackCachePolicy.CACHE_DIR).isEqualTo("media-downloads")
        assertThat(PlaybackCachePolicy.WRITE_DURING_PLAYBACK).isFalse()
        assertThat(PlaybackCachePolicy.flags()).isEqualTo(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}
