package com.ysydhc.bitmapcanary.tips

import android.text.TextUtils
import android.view.View

object LogBuilder {

    fun getViewIdOrName(view: View): String {
        var name = ""
        try {
            name = view.context.resources.getResourceName(view.id)
        } catch (e: Exception) {
            // ignore
        }
        if (TextUtils.isEmpty(name)) {
            name = view.javaClass.name
        }
        return name
    }




}