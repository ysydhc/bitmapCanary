package com.ysydhc.bitmapcanary.decoder

abstract class IDrawableDecoder<T> {

    abstract fun isHandle(t: Any): Boolean

    fun handle(t: Any): Pair<Int, Int>? {
        return decode(t as T)
    }

    abstract fun decode(t: T): Pair<Int, Int>?

}