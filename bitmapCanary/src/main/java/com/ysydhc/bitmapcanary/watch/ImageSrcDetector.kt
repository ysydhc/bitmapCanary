package com.ysydhc.bitmapcanary.watch

import android.view.View
import android.widget.ImageView
import com.ysydhc.bitmapcanary.TAG

class ImageSrcDetector : ViewDetector<ImageView>() {

    companion object {
        private const val LOG_TAG = TAG + "_Image"
    }

    override fun isCanDetector(view: View) = view is ImageView

    override fun detect(view: View?) {
        if (view !is ImageView) {
            return
        }
        handleDrawable(view.drawable, view, LOG_TAG)
    }
}