package com.ysydhc.bitmapcanary.utils

class LogRateLimiter(private val maxLogs: Int, private val timeWindowMillis: Long) {
    private val logTimestamps = mutableListOf<Long>()
    private var lastLogTime: Long = 0

    @Synchronized
    fun shouldLog(): Boolean {
        val currentTime = System.currentTimeMillis()
        logTimestamps.removeAll { it < currentTime - timeWindowMillis }
        return if (logTimestamps.size < maxLogs) {
            logTimestamps.add(currentTime)
            true
        } else {
            false
        }
    }

    @Synchronized
    fun logOncePer5s(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastLogTime >= 5000) { // 5 seconds
            lastLogTime = currentTime
            true
        } else {
            false
        }
    }
}
