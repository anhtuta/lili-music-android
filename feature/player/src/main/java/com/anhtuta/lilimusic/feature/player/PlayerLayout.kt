package com.anhtuta.lilimusic.feature.player

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Web `BaiHat.css` hides `#div_left_wrapper` at `max-width: 720px`.
 * `WindowSizeClass.Compact` is 600.dp, which would hide the cover earlier than the web.
 */
object PlayerLayout {
    val CompactMaxWidth = 720.dp

    fun isCompact(maxWidth: Dp): Boolean = maxWidth < CompactMaxWidth
}
