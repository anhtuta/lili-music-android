package com.anhtuta.lilimusic.feature.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.anhtuta.lilimusic.core.ui.palette.CoverPalette
import com.anhtuta.lilimusic.core.ui.palette.CoverPaletteColors

@Composable
fun rememberCoverPalette(imageUrl: String?): CoverPaletteColors? {
    val context = LocalContext.current
    var colors by remember(imageUrl) { mutableStateOf<CoverPaletteColors?>(null) }
    LaunchedEffect(imageUrl) {
        if (imageUrl == null) {
            colors = null
            return@LaunchedEffect
        }
        val request =
            ImageRequest
                .Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()
        val result = context.imageLoader.execute(request)
        val drawable = (result as? SuccessResult)?.drawable
        colors = drawable?.toBitmap()?.let(CoverPalette::fromBitmap)
    }
    return colors
}
