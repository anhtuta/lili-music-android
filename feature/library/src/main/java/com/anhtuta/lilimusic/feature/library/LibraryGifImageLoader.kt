package com.anhtuta.lilimusic.feature.library

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

private fun robolectricRuntime(): Boolean =
    Build.FINGERPRINT.contains("robolectric", ignoreCase = true) ||
        Build.MODEL.contains("robolectric", ignoreCase = true)

@Composable
internal fun rememberLibraryGifImageLoader(): ImageLoader {
    val context = LocalContext.current.applicationContext
    val useGifDecoder = robolectricRuntime() || Build.VERSION.SDK_INT < 28
    return remember(context, useGifDecoder) {
        ImageLoader
            .Builder(context)
            .components {
                if (useGifDecoder) {
                    add(GifDecoder.Factory())
                } else {
                    add(ImageDecoderDecoder.Factory())
                }
            }.build()
    }
}
