package com.ysydhc.bitmapcanary.watch

import com.ysydhc.bitmapcanary.decoder.CanaryBitmapDrawableDecoder
import com.ysydhc.bitmapcanary.decoder.CanaryDefDrawableDecoder
import com.ysydhc.bitmapcanary.decoder.CanaryNinePatchDrawableDecoder
import com.ysydhc.bitmapcanary.decoder.CanaryRoundedBitmapDrawableDecoder
import com.ysydhc.bitmapcanary.decoder.IDrawableDecoder

object DrawableDecoderFactory {

    private val decoderList = mutableListOf<IDrawableDecoder<*>>()


    private val DEF_DECODER = CanaryBitmapDrawableDecoder()
    private val DEF_ROUND_DECODER = CanaryRoundedBitmapDrawableDecoder()
    private val DEF_NINE_PATH_DECODER = CanaryNinePatchDrawableDecoder()

    private val FINAL_DECODER = CanaryDefDrawableDecoder()

    fun registerDef() {
        registerDrawableDecoder(DEF_DECODER)
        registerDrawableDecoder(DEF_ROUND_DECODER)
        registerDrawableDecoder(DEF_NINE_PATH_DECODER)
    }

    fun getDecoderList() = decoderList

    fun registerDrawableDecoder(decoder: IDrawableDecoder<*>) {
        if (decoderList.contains(decoder)) {
            return
        }
        decoderList.add(decoder)
    }

    fun clearDrawableDecoder() {
        decoderList.clear()
        ActivityDrawableWatcher.Companion.registerDrawableDecoder(DEF_DECODER)
        ActivityDrawableWatcher.Companion.registerDrawableDecoder(DEF_ROUND_DECODER)
        ActivityDrawableWatcher.Companion.registerDrawableDecoder(DEF_NINE_PATH_DECODER)
    }

    fun getFinalDecoder() = FINAL_DECODER

}