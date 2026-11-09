package com.anhtuta.lilimusic.feature.library

import androidx.compose.ui.unit.dp

/** Horizontal chrome shared by search, header actions, and row overflow. */
internal object LibraryChromeInsets {
    val Horizontal = 16.dp

    /** Trailing inset for header icons and row ⋮ to share the same right edge. */
    val TrailingAction = 4.dp

    /** Space reserved for the catalog scrollbar track at the list's trailing edge. */
    val ScrollbarTrack = 28.dp
}
