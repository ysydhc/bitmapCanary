package com.ysydhc.bitmapcanary.decoder

import androidx.core.graphics.drawable.RoundedBitmapDrawable

class CanaryRoundedBitmapDrawableDecoder : IDrawableDecoder<RoundedBitmapDrawable>() {
    override fun isHandle(t: Any): Boolean {
        return t is RoundedBitmapDrawable
    }

    override fun decode(t: RoundedBitmapDrawable): Pair<Int, Int>? {
        val w = t.bitmap?.width ?: 0
        val h = t.bitmap?.height ?: 0
        return Pair(w, h)
    }
}