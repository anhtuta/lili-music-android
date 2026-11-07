package com.anhtuta.lilimusic.feature.lyrics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface LyricCardCoverLoader {
    suspend fun load(url: String?): Bitmap?
}

@Singleton
class CoilLyricCardCoverLoader
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : LyricCardCoverLoader {
        override suspend fun load(url: String?): Bitmap? {
            if (url.isNullOrBlank()) {
                return null
            }
            val request =
                ImageRequest
                    .Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .build()
            val result = ImageLoader(context).execute(request)
            val drawable = (result as? SuccessResult)?.drawable ?: return null
            return (drawable as? BitmapDrawable)?.bitmap
        }
    }
