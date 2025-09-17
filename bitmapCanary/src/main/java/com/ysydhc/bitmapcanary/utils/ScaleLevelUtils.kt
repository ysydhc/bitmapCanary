package com.ysydhc.bitmapcanary.utils

import android.graphics.Color

/**
 * 比例等级判断工具类
 * 用于统一管理BitmapCanary中的比例等级判断逻辑
 */

/**
 * 默认比例阈值配置
 */
const val MINOR_SCALE = 1.05f      // 轻微超限
const val MODERATE_SCALE = 1.5f    // 中等超限
const val SEVERE_SCALE = 2.0f      // 严重超限

object ScaleLevelUtils {

    /**
     * 比例等级枚举
     */
    enum class ScaleLevel(val description: String, val color: Int) {
        NORMAL("正常", Color.TRANSPARENT),
        MINOR("轻微超限", Color.argb(140, 65, 134, 240)),      // 蓝色
        MODERATE("中等超限", Color.argb(140, 254, 198, 0)),     // 黄色
        SEVERE("严重超限", Color.argb(140, 234, 34, 50))       // 红色
    }

    /**
     * 根据缩放比例获取提示颜色
     * @param scale 缩放比例
     * @return 提示颜色
     */
    fun getTipColorByScale(scale: Float): Int {
        return getScaleLevel(scale).color
    }

    /**
     * 根据缩放比例获取比例等级
     * @param scale 缩放比例
     * @return 比例等级
     */
    fun getScaleLevel(scale: Float): ScaleLevel {
        return when {
            scale <= MINOR_SCALE -> ScaleLevel.NORMAL
            scale < MODERATE_SCALE -> ScaleLevel.MINOR
            scale < SEVERE_SCALE -> ScaleLevel.MODERATE
            else -> ScaleLevel.SEVERE
        }
    }

    /**
     * 根据缩放比例获取等级描述
     * @param scale 缩放比例
     * @return 等级描述
     */
    fun getScaleDescription(scale: Float): String {
        return getScaleLevel(scale).description
    }

    /**
     * 判断是否超过轻微超限阈值
     * @param scale 缩放比例
     * @return 是否超过轻微超限
     */
    fun isMinorExceeded(scale: Float): Boolean {
        return scale > MINOR_SCALE
    }

    /**
     * 判断是否超过中等超限阈值
     * @param scale 缩放比例
     * @return 是否超过中等超限
     */
    fun isModerateExceeded(scale: Float): Boolean {
        return scale >= MODERATE_SCALE
    }

    /**
     * 判断是否超过严重超限阈值
     * @param scale 缩放比例
     * @return 是否超过严重超限
     */
    fun isSevereExceeded(scale: Float): Boolean {
        return scale >= SEVERE_SCALE
    }

    /**
     * 获取自定义阈值的比例等级
     * @param scale 缩放比例
     * @param minorThreshold 轻微超限阈值
     * @param moderateThreshold 中等超限阈值
     * @param severeThreshold 严重超限阈值
     * @return 比例等级
     */
    fun getScaleLevelWithCustomThresholds(
        scale: Float,
        minorThreshold: Float,
        moderateThreshold: Float,
        severeThreshold: Float
    ): ScaleLevel {
        return when {
            scale <= minorThreshold -> ScaleLevel.NORMAL
            scale < moderateThreshold -> ScaleLevel.MINOR
            scale < severeThreshold -> ScaleLevel.MODERATE
            else -> ScaleLevel.SEVERE
        }
    }

    /**
     * 计算缩放比例
     * @param bitmapWidth Bitmap宽度
     * @param bitmapHeight Bitmap高度
     * @param viewWidth View宽度
     * @param viewHeight View高度
     * @return 缩放比例
     */
    fun calculateScale(
        bitmapWidth: Int,
        bitmapHeight: Int,
        viewWidth: Int,
        viewHeight: Int
    ): Float {
        if (viewWidth <= 0 || viewHeight <= 0) return 0f

        val widthScale = bitmapWidth.toFloat() / viewWidth
        val heightScale = bitmapHeight.toFloat() / viewHeight

        return maxOf(widthScale, heightScale)
    }
}

