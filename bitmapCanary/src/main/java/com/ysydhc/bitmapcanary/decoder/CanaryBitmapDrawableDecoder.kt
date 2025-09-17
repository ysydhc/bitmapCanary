package com.ysydhc.bitmapcanary.decoder

import android.graphics.drawable.BitmapDrawable

class CanaryBitmapDrawableDecoder : IDrawableDecoder<BitmapDrawable>() {
    override fun isHandle(t: Any): Boolean {
        return t is BitmapDrawable
    }

    override fun decode(t: BitmapDrawable): Pair<Int, Int>? {
        t.bitmap?.let { return Pair(it.width, it.height) }
        return null
    }
}