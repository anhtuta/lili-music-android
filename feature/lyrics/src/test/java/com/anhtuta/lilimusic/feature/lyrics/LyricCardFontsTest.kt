package com.anhtuta.lilimusic.feature.lyrics

import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import androidx.test.core.app.ApplicationProvider
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LyricCardFontsTest {
    @Test
    fun decorativeGoogleFontsAreBundled() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val fonts = LyricCardFonts(context)
        listOf(
            LyricCardFont.LOBSTER,
            LyricCardFont.PACIFICO,
            LyricCardFont.LAVISHLY_YOURS,
            LyricCardFont.BOREL,
        ).forEach { font ->
            val id = checkNotNull(LyricCardFontResources.id(font))
            assertThat(ResourcesCompat.getFont(context, id)).isNotNull()
            val bundled = fonts.typeface(font, bold = false)
            val italicSans = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
            assertThat(bundled).isNotEqualTo(italicSans)
        }
    }

    @Test
    fun systemFontsHaveNoBundledResource() {
        assertThat(LyricCardFontResources.id(LyricCardFont.OPEN_SANS)).isNull()
        assertThat(LyricCardFontResources.id(LyricCardFont.VERDANA)).isNull()
        assertThat(LyricCardFontResources.id(LyricCardFont.GEORGIA)).isNull()
    }

    @Test
    fun oflLicensesArePackaged() {
        assertThat(LyricCardFontResources.oflRawIds()).hasLength(4)
        LyricCardFontResources.oflRawIds().forEach { id ->
            assertThat(id).isNotEqualTo(0)
        }
    }

    @Test
    fun rendererUsesBundledTypefaceInsteadOfItalicFallback() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val lobster = LyricCardFonts(context).typeface(LyricCardFont.LOBSTER, bold = false)
        val bitmap =
            LyricCardRenderer.render(
                LyricCardRenderRequest(
                    settings =
                        LyricCardSettings(
                            fontFamily = LyricCardFont.LOBSTER,
                            useSongImage = false,
                        ),
                    selectedText = "Hello",
                    title = "Title",
                    artist = "Artist",
                    sizePx = 80,
                    typeface = lobster,
                ),
            )
        assertThat(bitmap.width).isEqualTo(80)
        assertThat(LyricCardRenderer.toJpeg(bitmap).size).isGreaterThan(32)
    }
}
