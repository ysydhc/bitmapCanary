package com.ysydhc.frescoext

import android.graphics.drawable.BitmapDrawable
import com.facebook.drawee.drawable.ForwardingDrawable
import com.ysydhc.bitmapcanary.decoder.IDrawableDecoder

class FrescoRootDrawableDetector : IDrawableDecoder<ForwardingDrawable>() {

    override fun isHandle(t: Any): Boolean {
        return t is ForwardingDrawable
    }

    override fun decode(t: ForwardingDrawable): Pair<Int, Int>? {
        val current = t.current
        if (current is BitmapDrawable) {
            current.bitmap?.let { b ->
                return Pair(b.width, b.height)
            }
        }
        return null
    }
}