package com.anhtuta.lilimusic.core.data.testdoubles

import com.anhtuta.lilimusic.core.datastore.LyricCardPreferences
import com.anhtuta.lilimusic.core.model.HexColor
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLyricCardPreferences(
    initial: LyricCardSettings = LyricCardSettings(),
) : LyricCardPreferences {
    private val state = MutableStateFlow(initial)
    override val preferences: Flow<LyricCardSettings> = state

    override suspend fun setTextAlign(align: LyricAlign) {
        state.value = state.value.copy(textAlign = align)
    }

    override suspend fun setTextColor(color: String) {
        state.value =
            state.value.copy(
                textColor = HexColor.normalize(color, LyricCardSettings.DEFAULT_TEXT_COLOR),
            )
    }

    override suspend fun setBold(enabled: Boolean) {
        state.value = state.value.copy(isBold = enabled)
    }

    override suspend fun setBackgroundColor(color: String) {
        state.value =
            state.value.copy(
                backgroundColor = HexColor.normalize(color, LyricCardSettings.DEFAULT_BACKGROUND),
            )
    }

    override suspend fun setUseSongImage(enabled: Boolean) {
        state.value = state.value.copy(useSongImage = enabled)
    }

    override suspend fun setOverlayOpacity(percent: Int) {
        state.value = state.value.copy(overlayOpacity = LyricCardSettings.clampOverlay(percent))
    }

    override suspend fun setBrightnessBoost(percent: Int) {
        state.value = state.value.copy(brightnessBoost = LyricCardSettings.clampBrightness(percent))
    }

    override suspend fun setFontFamily(font: LyricCardFont) {
        state.value = state.value.copy(fontFamily = font)
    }

    override suspend fun setFontSize(size: Int) {
        state.value = state.value.copy(fontSize = LyricCardSettings.clampFontSize(size))
    }
}
