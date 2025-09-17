package com.ysydhc.bitmapcanary.watch

import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.Log
import android.view.View
import com.ysydhc.bitmapcanary.ext.viewInfo
import com.ysydhc.bitmapcanary.utils.MINOR_SCALE
import com.ysydhc.bitmapcanary.utils.MarkScaleViewUtils

abstract class ViewDetector<T : View>() {
    /**
     * 是否能监控View
     */
    abstract fun isCanDetector(view: View): Boolean

    /**
     * 开始监控
     */
    protected abstract fun detect(view: View?)

    fun startDetect(view: View?) {
        detect(view)
    }
}

fun <T : View> handleDrawable(drawable: Drawable?, view: T, tag: String) {
    val curDrawable = (if (drawable is StateListDrawable) drawable.current else drawable) ?: return
    var isCanHandle = false
    var size: Pair<Int, Int>? = null
    DrawableDecoderFactory.getDecoderList().forEach { decoder ->
        if (decoder.isHandle(curDrawable)) {
            isCanHandle = true
            size = decoder.handle(curDrawable)
            if (size != null) {
                return@forEach
            }
        }
    }
    if (size == null) {
        size = DrawableDecoderFactory.getFinalDecoder().handle(curDrawable)
    }
    if (size != null) {
        if (size.second > view.height * MINOR_SCALE || size.first > view.width * MINOR_SCALE) {
            MarkScaleViewUtils.markScaleView(size.first, size.second, view)
        }
    }

    // 处理日志
    val sb = StringBuilder()
    if (!isCanHandle) {
        sb.append(" ViewSize:" + view.width + "x" + view.height)
    }
    if (size == null) {
        sb.append(" can't decode size ")
    } else {
        sb.append(" BitmapSize:" + size.first + "x" + size.second)
    }
    sb.append(" Drawable Class:${curDrawable.javaClass.name}")
    Log.d(tag, viewInfo(view) + sb.toString())
}