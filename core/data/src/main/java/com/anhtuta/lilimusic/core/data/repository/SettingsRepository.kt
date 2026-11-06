package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<PlayerSettings>

    suspend fun setLyricAlign(align: LyricAlign)

    suspend fun setLyricTheme(theme: LyricTheme)

    suspend fun setKaraokeMode(enabled: Boolean)

    suspend fun setBlurMode(enabled: Boolean)

    suspend fun setGiganticLine(enabled: Boolean)

    suspend fun setWordAppear(enabled: Boolean)

    suspend fun setLineAppear(enabled: Boolean)

    suspend fun setTranslate(enabled: Boolean)

    suspend fun setAppTheme(theme: AppTheme)
}
