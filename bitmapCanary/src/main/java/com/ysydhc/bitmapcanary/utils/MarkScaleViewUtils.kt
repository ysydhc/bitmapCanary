package com.ysydhc.bitmapcanary.utils

import android.util.Log
import android.view.View
import android.view.ViewOverlay
import com.ysydhc.bitmapcanary.TAG
import com.ysydhc.bitmapcanary.ext.viewInfo
import com.ysydhc.bitmapcanary.tips.TextDetectDrawable

object MarkScaleViewUtils {

    fun <T : View> markScaleView(bWidth: Int, bHeight: Int, view: T) {
        Log.d(
            TAG + "RES",
            viewInfo(view) + " ViewSize:${view.width}x${view.height}" + " BitmapSize:${bWidth}x${bHeight}"
        )
        // 使用公共工具类计算缩放比例
        val scale = ScaleLevelUtils.calculateScale(bWidth, bHeight, view.width, view.height)

        drawTips(
            view, text = String.format("%.1f", scale),
            DrawableDetectUtil.Companion.getTipColorByScale(scale)
        )
    }

    fun <T : View> drawTips(view: T, text: String, color: Int) {
        val overlay: ViewOverlay = view.getOverlay()
        overlay.clear()
        val detectDrawable = TextDetectDrawable()
        detectDrawable.setText(text)
        detectDrawable.setBgColor(color)
        detectDrawable.setBounds(0, 0, view.width, view.height)
        overlay.add(detectDrawable)
    }

}