package com.anhtuta.lilimusic.feature.lyrics

import android.graphics.Bitmap
import android.graphics.Typeface
import com.anhtuta.lilimusic.core.model.LyricCardSettings

data class LyricCardRenderRequest(
    val settings: LyricCardSettings,
    val selectedText: String,
    val title: String,
    val artist: String,
    val cover: Bitmap? = null,
    val sizePx: Int = LyricCardSettings.EXPORT_SIZE_PX,
    val typeface: Typeface? = null,
)
