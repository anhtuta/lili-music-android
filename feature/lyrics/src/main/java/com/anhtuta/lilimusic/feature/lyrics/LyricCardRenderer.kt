package com.anhtuta.lilimusic.feature.lyrics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import com.anhtuta.lilimusic.core.model.HexColor
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardSettings
import java.io.ByteArrayOutputStream
import kotlin.math.max

/**
 * Square JPEG lyric card. Overlay order matches web CSS backgrounds:
 * cover → black overlay (`overlayOpacity`) → white brightness (`brightnessBoost`).
 */
object LyricCardRenderer {
    private const val JPEG_QUALITY = 90
    private const val FOOTER_CSS_PX = 13f
    private const val FOOTER_BORDER_ALPHA = 0.3f

    fun render(request: LyricCardRenderRequest): Bitmap {
        val size = request.sizePx.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val settings = request.settings
        val background = color(settings.backgroundColor, LyricCardSettings.DEFAULT_BACKGROUND)
        bitmap.eraseColor(background)
        val canvas = Canvas(bitmap)
        if (settings.useSongImage) {
            request.cover?.let { drawCover(canvas, it, size, settings) }
        }
        val scale = size / LyricCardSettings.WEB_CARD_CSS_PX.toFloat()
        val padding = LyricCardSettings.WEB_PADDING_CSS_PX * scale
        val textPaint = textPaint(settings, scale, request.typeface)
        val footer = footerLayout(request, textPaint, size, padding, scale)
        val lyricBottom = size - padding - footer.height - padding / 2f
        val lyricWidth = (size - 2f * padding).toInt().coerceAtLeast(1)
        val lyricLayout =
            staticLayout(
                text = request.selectedText,
                paint = textPaint,
                width = lyricWidth,
                align = alignment(settings.textAlign),
                maxHeight = (lyricBottom - padding).toInt().coerceAtLeast(1),
            )
        canvas.save()
        canvas.clipRect(padding, padding, size - padding, lyricBottom)
        canvas.translate(padding, padding)
        lyricLayout.draw(canvas)
        canvas.restore()
        val footerTop = size - padding - footer.height
        drawFooterRule(canvas, padding, size, footerTop, textPaint.color)
        canvas.save()
        canvas.translate(padding, footerTop)
        footer.draw(canvas)
        canvas.restore()
        return bitmap
    }

    fun toJpeg(bitmap: Bitmap): ByteArray {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        return out.toByteArray()
    }

    private fun drawCover(
        canvas: Canvas,
        cover: Bitmap,
        size: Int,
        settings: LyricCardSettings,
    ) {
        val scale =
            max(
                size / cover.width.coerceAtLeast(1).toFloat(),
                size / cover.height.coerceAtLeast(1).toFloat(),
            )
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        matrix.postTranslate(
            (size - cover.width * scale) / 2f,
            (size - cover.height * scale) / 2f,
        )
        val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(cover, matrix, bitmapPaint)
        fill(canvas, size, Color.argb(LyricCardSettings.overlayAlphaByte(settings.overlayOpacity), 0, 0, 0))
        fill(
            canvas,
            size,
            Color.argb(LyricCardSettings.brightnessAlphaByte(settings.brightnessBoost), 255, 255, 255),
        )
    }

    private fun fill(
        canvas: Canvas,
        size: Int,
        color: Int,
    ) {
        if (Color.alpha(color) == 0) {
            return
        }
        val paint = Paint()
        paint.color = color
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
    }

    private fun footerLayout(
        request: LyricCardRenderRequest,
        bodyPaint: TextPaint,
        size: Int,
        padding: Float,
        scale: Float,
    ): StaticLayout {
        val footerPaint = TextPaint(bodyPaint)
        footerPaint.textSize = FOOTER_CSS_PX * scale
        footerPaint.typeface = Typeface.create(bodyPaint.typeface, Typeface.NORMAL)
        footerPaint.isFakeBoldText = true
        val width = (size - 2f * padding).toInt().coerceAtLeast(1)
        val title = request.title.trim()
        val artist = request.artist.trim()
        val block =
            buildString {
                if (title.isNotEmpty()) append(title)
                if (artist.isNotEmpty()) {
                    if (isNotEmpty()) append('\n')
                    append(artist)
                }
            }
        return staticLayout(
            text = block,
            paint = footerPaint,
            width = width,
            align = alignment(request.settings.textAlign),
            maxHeight = (size / 3).coerceAtLeast(1),
        )
    }

    private fun drawFooterRule(
        canvas: Canvas,
        padding: Float,
        size: Int,
        footerTop: Float,
        textColor: Int,
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.argb((FOOTER_BORDER_ALPHA * Color.alpha(textColor)).toInt(), 255, 255, 255)
        paint.strokeWidth = 1f * size / LyricCardSettings.WEB_CARD_CSS_PX
        val y = footerTop - 10f * size / LyricCardSettings.WEB_CARD_CSS_PX
        canvas.drawLine(padding, y, size - padding, y, paint)
    }

    private fun textPaint(
        settings: LyricCardSettings,
        scale: Float,
        typeface: Typeface?,
    ): TextPaint {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color(settings.textColor, LyricCardSettings.DEFAULT_TEXT_COLOR)
        paint.textSize = settings.fontSize * scale
        paint.typeface = typeface ?: LyricCardFonts.systemTypeface(settings.fontFamily, settings.isBold)
        return paint
    }

    private fun alignment(align: LyricAlign): Layout.Alignment =
        when (align) {
            LyricAlign.LEFT -> Layout.Alignment.ALIGN_NORMAL
            LyricAlign.CENTER -> Layout.Alignment.ALIGN_CENTER
            LyricAlign.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
        }

    private fun staticLayout(
        text: String,
        paint: TextPaint,
        width: Int,
        align: Layout.Alignment,
        maxHeight: Int,
    ): StaticLayout {
        val layout =
            StaticLayout.Builder
                .obtain(text, 0, text.length, paint, width)
                .setAlignment(align)
                .setIncludePad(false)
                .setEllipsize(TextUtils.TruncateAt.END)
                .build()
        if (layout.height <= maxHeight) {
            return layout
        }
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width)
            .setAlignment(align)
            .setIncludePad(false)
            .setMaxLines(maxLinesFor(layout, maxHeight).coerceAtLeast(1))
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()
    }

    private fun maxLinesFor(
        layout: StaticLayout,
        maxHeight: Int,
    ): Int {
        var count = 0
        while (count < layout.lineCount && layout.getLineBottom(count) <= maxHeight) {
            count += 1
        }
        return count.coerceAtLeast(1)
    }

    private fun color(
        hex: String,
        default: String,
    ): Int {
        val rgb = HexColor.rgb(hex, default)
        return Color.rgb(HexColor.red(rgb), HexColor.green(rgb), HexColor.blue(rgb))
    }
}
