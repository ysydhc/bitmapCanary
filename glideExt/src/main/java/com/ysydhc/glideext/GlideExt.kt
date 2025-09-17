package com.ysydhc.glideext

import com.ysydhc.bitmapcanary.watch.ActivityDrawableWatcher.Companion.registerDrawableDecoder

object GlideExt {

    fun register() {
        registerDrawableDecoder(CanaryGlideBitmapDrawableDecoder())
        registerDrawableDecoder(CanaryGlideGifDrawableDecoder())
    }

}