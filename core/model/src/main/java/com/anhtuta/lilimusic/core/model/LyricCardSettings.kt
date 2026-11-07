package com.anhtuta.lilimusic.core.model

/**
 * Web `LyricDownloadModal` defaults (`getSetting('lyricCard_*')` fallbacks).
 *
 * Card text align defaults to **left** (web). That is independent of playground
 * [PlayerSettings.lyricAlign], which defaults to center.
 */
data class LyricCardSettings(
    val textAlign: LyricAlign = LyricAlign.LEFT,
    val textColor: String = DEFAULT_TEXT_COLOR,
    val isBold: Boolean = false,
    val backgroundColor: String = DEFAULT_BACKGROUND,
    val useSongImage: Boolean = true,
    val overlayOpacity: Int = DEFAULT_OVERLAY,
    val brightnessBoost: Int = DEFAULT_BRIGHTNESS,
    val fontFamily: LyricCardFont = LyricCardFont.OPEN_SANS,
    val fontSize: Int = DEFAULT_FONT_SIZE,
) {
    companion object {
        const val DEFAULT_TEXT_COLOR = "#ffffff"
        const val DEFAULT_BACKGROUND = "#667eea"
        const val DEFAULT_OVERLAY = 60
        const val DEFAULT_BRIGHTNESS = 0
        const val DEFAULT_FONT_SIZE = 14
        const val MIN_OVERLAY = 0
        const val MAX_OVERLAY = 80
        const val OVERLAY_STEP = 5
        const val MIN_BRIGHTNESS = 0
        const val MAX_BRIGHTNESS = 60
        const val BRIGHTNESS_STEP = 5
        const val MIN_FONT_SIZE = 5
        const val MAX_FONT_SIZE = 40
        const val WEB_CARD_CSS_PX = 450
        const val WEB_PADDING_CSS_PX = 40
        const val EXPORT_PIXEL_RATIO = 4
        const val EXPORT_SIZE_PX = WEB_CARD_CSS_PX * EXPORT_PIXEL_RATIO

        val backgroundPresets: List<Pair<String, String>> =
            listOf(
                "#667eea" to "Purple Gradient",
                "#601f9e" to "Dark Purple",
                "#1a1a2e" to "Dark Blue",
                "#0f3460" to "Navy",
                "#e94560" to "Pink",
                "#ff6b6b" to "Red",
                "#4ecdc4" to "Teal",
                "#45b7d1" to "Cyan",
            )

        fun clampOverlay(value: Int): Int = value.coerceIn(MIN_OVERLAY, MAX_OVERLAY)

        fun clampBrightness(value: Int): Int = value.coerceIn(MIN_BRIGHTNESS, MAX_BRIGHTNESS)

        fun clampFontSize(value: Int): Int = value.coerceIn(MIN_FONT_SIZE, MAX_FONT_SIZE)

        fun overlayAlphaByte(percent: Int): Int = (clampOverlay(percent) / 100f * 255f).toInt()

        fun brightnessAlphaByte(percent: Int): Int = (clampBrightness(percent) / 100f * 255f).toInt()

        fun parseAlign(value: String?): LyricAlign = LyricAlign.entries.find { it.storageValue == value } ?: LyricAlign.LEFT
    }
}
