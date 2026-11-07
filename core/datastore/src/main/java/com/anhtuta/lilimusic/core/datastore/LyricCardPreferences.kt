package com.anhtuta.lilimusic.core.datastore

import com.anhtuta.lilimusic.core.model.HexColor
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings

data class LyricCardPreferenceSnapshot(
    val textAlign: String? = null,
    val textColor: String? = null,
    val isBold: Boolean? = null,
    val backgroundColor: String? = null,
    val useSongImage: Boolean? = null,
    val overlayOpacity: Int? = null,
    val brightnessBoost: Int? = null,
    val fontFamily: String? = null,
    val fontSize: Int? = null,
)

object LyricCardPreferencesParser {
    fun parse(raw: LyricCardPreferenceSnapshot): LyricCardSettings {
        val defaults = LyricCardSettings()
        return LyricCardSettings(
            textAlign = LyricCardSettings.parseAlign(raw.textAlign),
            textColor = HexColor.normalize(raw.textColor, defaults.textColor),
            isBold = raw.isBold ?: defaults.isBold,
            backgroundColor = HexColor.normalize(raw.backgroundColor, defaults.backgroundColor),
            useSongImage = raw.useSongImage ?: defaults.useSongImage,
            overlayOpacity = LyricCardSettings.clampOverlay(raw.overlayOpacity ?: defaults.overlayOpacity),
            brightnessBoost = LyricCardSettings.clampBrightness(raw.brightnessBoost ?: defaults.brightnessBoost),
            fontFamily = LyricCardFont.fromStorage(raw.fontFamily),
            fontSize = LyricCardSettings.clampFontSize(raw.fontSize ?: defaults.fontSize),
        )
    }
}
