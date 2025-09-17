package com.ysydhc.bitmapcanary.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.core.view.isGone
import androidx.core.view.isNotEmpty
import com.ysydhc.bitmapcanary.watch.DetectorFactory

class DrawableDetectUtil {
    companion object {

        fun detectDrawableSize(rootView: ViewGroup?) {
            if (rootView == null || rootView.isEmpty()) {
                return
            }
            detectDrawable(rootView)
        }

        fun detectDrawable(view: View?) {
            if (view == null || view.isGone || view.height == 0 || view.width == 0) {
                return
            }
            for (detector in DetectorFactory.getDetectorList()) {
                if (detector.isCanDetector(view)) {
                    detector.startDetect(view)
                }
            }
            // 遍历ViewTree
            if (view is ViewGroup && view.isNotEmpty()) {
                val viewGroup = view
                for (i in 0..<viewGroup.childCount) {
                    val childView = viewGroup.getChildAt(i)
                    detectDrawable(childView)
                }
            }
        }

        /**
         * 根据缩放比例获取提示颜色
         * 使用公共工具类的方法
         */
        fun getTipColorByScale(scale: Float): Int {
            return ScaleLevelUtils.getTipColorByScale(scale)
        }

        /**
         * 获取比例等级描述
         */
        fun getScaleDescription(scale: Float): String {
            return ScaleLevelUtils.getScaleDescription(scale)
        }

        /**
         * 判断是否超过轻微超限阈值
         */
        fun isMinorExceeded(scale: Float): Boolean {
            return ScaleLevelUtils.isMinorExceeded(scale)
        }

        /**
         * 判断是否超过中等超限阈值
         */
        fun isModerateExceeded(scale: Float): Boolean {
            return ScaleLevelUtils.isModerateExceeded(scale)
        }

        /**
         * 判断是否超过严重超限阈值
         */
        fun isSevereExceeded(scale: Float): Boolean {
            return ScaleLevelUtils.isSevereExceeded(scale)
        }
    }
}