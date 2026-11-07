package com.anhtuta.lilimusic.core.model

/**
 * Web `LyricDownloadModal` / `CardConfiguration` font-family `<select>` values.
 * Decorative Google Fonts (Lavishly Yours, Lobster, Pacifico, Borel) are bundled as `res/font`.
 * Open Sans, Verdana, and Georgia use the system sans/serif.
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
