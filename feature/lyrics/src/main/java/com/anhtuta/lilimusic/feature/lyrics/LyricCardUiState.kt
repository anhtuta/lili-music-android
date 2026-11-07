package com.anhtuta.lilimusic.feature.lyrics

import android.net.Uri
import com.anhtuta.lilimusic.core.model.LyricCardSettings

enum class LyricCardStep {
    HIDDEN,
    SELECT,
    CONFIGURE,
}

data class LyricCardSelectableLine(
    val id: Int,
    val text: String,
    val selected: Boolean,
)

data class LyricCardUiState(
    val step: LyricCardStep = LyricCardStep.HIDDEN,
    val showNoLyricWarning: Boolean = false,
    val songTitle: String = "",
    val songArtist: String = "",
    val coverUrl: String? = null,
    val lines: List<LyricCardSelectableLine> = emptyList(),
    val selectedText: String = "",
    val config: LyricCardSettings = LyricCardSettings(),
    val shareUri: Uri? = null,
    val shareFileName: String? = null,
    val copied: Boolean = false,
    val shareFailed: Boolean = false,
) {
    val canGenerate: Boolean
        get() = selectedText.isNotBlank()
}
