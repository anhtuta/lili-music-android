package com.anhtuta.lilimusic.feature.settings

import com.anhtuta.lilimusic.core.data.repository.SettingsRepository
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository(
    initial: PlayerSettings = PlayerSettings(),
) : SettingsRepository {
    private val state = MutableStateFlow(initial)
    override val settings: Flow<PlayerSettings> = state

    override suspend fun setLyricAlign(align: LyricAlign) {
        state.value = state.value.copy(lyricAlign = align)
    }

    override suspend fun setLyricTheme(theme: LyricTheme) {
        state.value = state.value.copy(lyricTheme = theme)
    }

    override suspend fun setKaraokeMode(enabled: Boolean) {
        state.value = state.value.copy(karaokeMode = enabled)
    }

    override suspend fun setBlurMode(enabled: Boolean) {
        state.value = state.value.copy(blurMode = enabled)
    }

    override suspend fun setGiganticLine(enabled: Boolean) {
        state.value = state.value.copy(giganticLine = enabled)
    }

    override suspend fun setWordAppear(enabled: Boolean) {
        state.value = state.value.copy(wordAppear = enabled)
    }

    override suspend fun setLineAppear(enabled: Boolean) {
        state.value = state.value.copy(lineAppear = enabled)
    }

    override suspend fun setTranslate(enabled: Boolean) {
        state.value = state.value.copy(translate = enabled)
    }

    override suspend fun setAppTheme(theme: AppTheme) {
        state.value = state.value.copy(appTheme = theme)
    }
}
