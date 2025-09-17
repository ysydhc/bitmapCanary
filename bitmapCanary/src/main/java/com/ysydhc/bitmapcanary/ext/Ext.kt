package com.ysydhc.bitmapcanary.ext

import android.view.View
import com.ysydhc.bitmapcanary.tips.LogBuilder


fun <T : View> viewInfo(view: T): String {
    return LogBuilder.getViewIdOrName(view)
}