package com.ysydhc.bitmapcanary.watch

import android.view.View
import com.ysydhc.bitmapcanary.TAG


class BackgroundDetector : ViewDetector<View>() {

    companion object {
        private const val LOG_TAG = TAG + "_Background";
    }

    override fun isCanDetector(view: View): Boolean {
        return true
    }

    override fun detect(view: View?) {
        view ?: return
        handleDrawable(view.background, view, LOG_TAG)
    }

}