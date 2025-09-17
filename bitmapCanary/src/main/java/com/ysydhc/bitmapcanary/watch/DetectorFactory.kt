package com.ysydhc.bitmapcanary.watch


class DetectorFactory {
    companion object {

        private val detectorCache = mutableListOf<ViewDetector<*>>()

        init {
            detectorCache.add(BackgroundDetector())
            detectorCache.add(ImageSrcDetector())
        }


        fun getDetectorList(): List<ViewDetector<*>> {
            return detectorCache
        }

        /**
         * 允许外部注册自定义的Detector
         */
        @JvmStatic
        fun registerCustomDetector(detector: ViewDetector<*>) {
            if (detectorCache.contains(detector)) {
                return
            }
            detectorCache.add(detector)
        }
    }

}