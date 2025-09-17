package com.ysydhc.glideext

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable
import com.ysydhc.bitmapcanary.decoder.IDrawableDecoder

class CanaryGlideBitmapDrawableDecoder : IDrawableDecoder<GlideBitmapDrawable>() {

    override fun isHandle(t: Any): Boolean {
        return t is GlideBitmapDrawable
    }

    override fun decode(t: GlideBitmapDrawable): Pair<Int, Int>? {
        t.bitmap?.let { b ->
            return Pair(b.width, b.height)
        }
        return null
    }
}