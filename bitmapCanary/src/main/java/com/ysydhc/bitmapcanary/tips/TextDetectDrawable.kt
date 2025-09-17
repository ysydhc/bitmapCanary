package com.ysydhc.bitmapcanary.tips

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import androidx.core.graphics.withTranslation
import kotlin.math.min

class TextDetectDrawable : Drawable() {
    private val textPaint: Paint = TextPaint()
    private var mText = ""
    private var mBgColor = Color.TRANSPARENT

    init {
        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
    }

    fun setText(text: String) {
        this.mText = text
    }

    fun setBgColor(bgColor: Int) {
        this.mBgColor = bgColor
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        canvas.withTranslation(bounds.left.toFloat(), bounds.top.toFloat()) {
            val fontSize =
                (min(bounds.height().toDouble(), bounds.width().toDouble()) / 2).toInt()
            textPaint.textSize = fontSize.toFloat()
            canvas.drawColor(mBgColor)
            canvas.drawText(
                mText,
                bounds.centerX().toFloat(),
                bounds.height() / 2 - (textPaint.descent() + textPaint.ascent()) / 2,
                textPaint
            )
        }
    }

    override fun setAlpha(i: Int) {
        textPaint.alpha = i
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        textPaint.setColorFilter(colorFilter)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}