package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import kotlinx.coroutines.flow.Flow

interface LyricCardSettingsRepository {
    val settings: Flow<LyricCardSettings>

    suspend fun setTextAlign(align: LyricAlign)

    suspend fun setTextColor(color: String)

    suspend fun setBold(enabled: Boolean)

    suspend fun setBackgroundColor(color: String)

    suspend fun setUseSongImage(enabled: Boolean)

    suspend fun setOverlayOpacity(percent: Int)

    suspend fun setBrightnessBoost(percent: Int)

    suspend fun setFontFamily(font: LyricCardFont)

    suspend fun setFontSize(size: Int)
}
