package com.anhtuta.lilimusic.core.model

/**
 * Web `LyricDownloadModal` font-family `<select>` values.
 * Android maps these names to system typefaces; decorative Google Fonts are not bundled.
 */
enum class LyricCardFont(
    val storageValue: String,
) {
    OPEN_SANS("Open Sans"),
    VERDANA("Verdana"),
    GEORGIA("Georgia"),
    LAVISHLY_YOURS("Lavishly Yours"),
    LOBSTER("Lobster"),
    PACIFICO("Pacifico"),
    BOREL("Borel"),
    ;

    val isSerif: Boolean
        get() = this == GEORGIA

    val isScript: Boolean
        get() =
            this == LAVISHLY_YOURS ||
                this == LOBSTER ||
                this == PACIFICO ||
                this == BOREL

    companion object {
        fun fromStorage(value: String?): LyricCardFont = entries.find { it.storageValue == value } ?: OPEN_SANS
    }
}
