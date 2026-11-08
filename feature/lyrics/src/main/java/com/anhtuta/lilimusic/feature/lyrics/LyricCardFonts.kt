package com.anhtuta.lilimusic.feature.lyrics

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.content.res.ResourcesCompat
import com.anhtuta.lilimusic.core.model.LyricCardFont
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

object LyricCardFontResources {
    fun id(font: LyricCardFont): Int? =
        when (font) {
            LyricCardFont.LOBSTER -> R.font.lobster_regular
            LyricCardFont.PACIFICO -> R.font.pacifico_regular
            LyricCardFont.LAVISHLY_YOURS -> R.font.lavishly_yours_regular
            LyricCardFont.BOREL -> R.font.borel_regular
            else -> null
        }

    fun composeFamily(font: LyricCardFont): FontFamily {
        val resource = id(font)
        return when {
            resource != null -> FontFamily(Font(resource))
            font.isSerif -> FontFamily.Serif
            else -> FontFamily.SansSerif
        }
    }

    fun oflRawIds(): IntArray =
        intArrayOf(
            R.raw.ofl_lobster_regular,
            R.raw.ofl_pacifico_regular,
            R.raw.ofl_lavishly_yours_regular,
            R.raw.ofl_borel_regular,
        )
}

@Singleton
class LyricCardFonts
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun typeface(
            font: LyricCardFont,
            bold: Boolean,
        ): Typeface {
            val bundled = LyricCardFontResources.id(font)?.let { ResourcesCompat.getFont(context, it) }
            if (bundled != null) {
                return if (bold) Typeface.create(bundled, Typeface.BOLD) else bundled
            }
            return systemTypeface(font, bold)
        }

        companion object {
            fun systemTypeface(
                font: LyricCardFont,
                bold: Boolean,
            ): Typeface {
                val family =
                    when {
                        font.isSerif -> Typeface.SERIF
                        else -> Typeface.SANS_SERIF
                    }
                val style = if (bold) Typeface.BOLD else Typeface.NORMAL
                return Typeface.create(family, style)
            }
        }
    }
