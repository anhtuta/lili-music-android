package com.anhtuta.lilimusic.feature.lyrics

import android.graphics.Bitmap
import android.graphics.Color
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LyricCardRendererTest {
    @Test
    fun solidBackgroundIsSquareAndMatchesPreset() {
        val bitmap =
            LyricCardRenderer.render(
                LyricCardRenderRequest(
                    settings = LyricCardSettings(useSongImage = false, backgroundColor = "#667eea"),
                    selectedText = "Hello",
                    title = "Title",
                    artist = "Artist",
                    sizePx = 100,
                ),
            )
        assertThat(bitmap.width).isEqualTo(100)
        assertThat(bitmap.height).isEqualTo(100)
        assertThat(bitmap.getPixel(0, 0)).isEqualTo(Color.rgb(0x66, 0x7e, 0xea))
        val jpeg = LyricCardRenderer.toJpeg(bitmap)
        assertThat(jpeg.size).isGreaterThan(32)
        val decoded = android.graphics.BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size)
        assertThat(decoded.width).isEqualTo(100)
    }

    @Test
    fun songImageCoverStillExportsSquareJpeg() {
        val cover = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
        cover.eraseColor(Color.WHITE)
        val bitmap =
            LyricCardRenderer.render(
                LyricCardRenderRequest(
                    settings =
                        LyricCardSettings(
                            useSongImage = true,
                            overlayOpacity = 60,
                            brightnessBoost = 10,
                        ),
                    selectedText = "Hi",
                    title = "T",
                    artist = "A",
                    cover = cover,
                    sizePx = 40,
                ),
            )
        assertThat(bitmap.width).isEqualTo(40)
        assertThat(bitmap.height).isEqualTo(40)
        assertThat(LyricCardRenderer.toJpeg(bitmap).size).isGreaterThan(16)
    }
}
