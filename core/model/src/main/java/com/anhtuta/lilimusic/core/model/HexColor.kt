package com.anhtuta.lilimusic.core.model

/**
 * `#rrggbb` helpers for lyric-card colour pickers (web `<input type="color">`).
 */
object HexColor {
    private val hex6 = Regex("^#?[0-9a-fA-F]{6}$")

    fun normalize(
        value: String?,
        default: String,
    ): String {
        val trimmed = value?.trim().orEmpty()
        if (!hex6.matches(trimmed)) {
            return default
        }
        val digits = trimmed.removePrefix("#")
        return "#${digits.lowercase()}"
    }

    fun rgb(
        value: String?,
        default: String,
    ): Int {
        val hex = normalize(value, default).removePrefix("#")
        return hex.toInt(16)
    }

    fun red(rgb: Int): Int = (rgb shr 16) and 0xFF

    fun green(rgb: Int): Int = (rgb shr 8) and 0xFF

    fun blue(rgb: Int): Int = rgb and 0xFF
}
