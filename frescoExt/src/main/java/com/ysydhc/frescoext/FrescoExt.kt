package com.ysydhc.frescoext

import com.ysydhc.bitmapcanary.watch.ActivityDrawableWatcher.Companion.registerDrawableDecoder

object FrescoExt {

    fun register() {
        registerDrawableDecoder(FrescoRootDrawableDetector())
    }

}