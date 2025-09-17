package com.ysydhc.bitmapcanary.decoder

import android.graphics.drawable.NinePatchDrawable

class CanaryNinePatchDrawableDecoder : IDrawableDecoder<NinePatchDrawable>() {
    override fun isHandle(t: Any): Boolean {
        return t is NinePatchDrawable
    }

    override fun decode(t: NinePatchDrawable): Pair<Int, Int>? {
        val width = t.current.intrinsicWidth
        val height = t.current.intrinsicHeight
        return Pair(width, height)
    }
}