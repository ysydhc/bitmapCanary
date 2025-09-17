package com.ysydhc.glideext

import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.ysydhc.bitmapcanary.decoder.IDrawableDecoder

class CanaryGlideGifDrawableDecoder : IDrawableDecoder<GifDrawable>() {
    override fun isHandle(t: Any): Boolean {
        return t is GifDrawable
    }

    override fun decode(t: GifDrawable): Pair<Int, Int>? {
        t.firstFrame?.let {
            return Pair(it.width, it.height)
        }
        val cur = t.current
        if (cur is BitmapDrawable) {
            cur.bitmap?.takeIf { it != null }?.let {
                return Pair(it.width, it.height)
            }
        }
        return null
    }
}