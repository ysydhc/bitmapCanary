package com.ysydhc.bitmapcanary.decoder

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class CanaryDefDrawableDecoder : IDrawableDecoder<Drawable>() {
    override fun isHandle(t: Any): Boolean {
        return true
    }

    override fun decode(t: Drawable): Pair<Int, Int>? {
        if (t is BitmapDrawable) {
            val w = t.bitmap?.width ?: 0
            val h = t.bitmap?.height ?: 0
            return if (w > 0 && h > 0) Pair(w, h) else null
        }
        val current = t.current
        if (current == t) {
            return null
        }
        return decode(current)
    }
}