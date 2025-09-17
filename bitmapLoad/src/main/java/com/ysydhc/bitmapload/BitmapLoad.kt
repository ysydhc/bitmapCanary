package com.ysydhc.bitmapload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.Executors

/**
 * Bitmap处理器接口
 * 用于自定义图片处理逻辑，如圆角、裁切等
 */
interface BitmapProcessor {
    /**
     * 处理Bitmap
     * @param bitmap 原始Bitmap
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     * @return 处理后的Bitmap
     */
    fun process(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap
}

/**
 * 默认的Bitmap处理器
 * 提供基本的图片处理功能
 */
class DefaultBitmapProcessor : BitmapProcessor {
    override fun process(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return bitmap
    }
}

/**
 * BitmapLoad - 智能图片加载工具类
 * 根据View大小自动加载对应尺寸的图片，避免内存浪费
 *
 * 使用方式：
 * BitmapLoad.with(context)
 *     .load(resId/path)
 *     .into(view)
 */
class BitmapLoad private constructor(private val context: Context) {

    private var source: Any? = null
    private var targetView: View? = null
    private var placeholder: Drawable? = null
    private var errorDrawable: Drawable? = null
    private var bitmapProcessor: BitmapProcessor = DefaultBitmapProcessor()
    private var listener: BitmapLoadListener? = null
    private var isCancelled: Boolean = false

    companion object {
        private val executor = Executors.newCachedThreadPool()
        private val mainHandler = Handler(Looper.getMainLooper())

        /**
         * 开始构建BitmapLoad实例
         */
        fun with(context: Context): BitmapLoad {
            return BitmapLoad(context)
        }
    }

    /**
     * 设置图片源
     */
    fun load(source: Any): BitmapLoad {
        this.source = source
        return this
    }

    /**
     * 设置占位图
     */
    fun placeholder(resId: Int): BitmapLoad {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.placeholder = context.getDrawable(resId)
        }
        return this
    }

    /**
     * 设置占位图
     */
    fun placeholder(drawable: Drawable): BitmapLoad {
        this.placeholder = drawable
        return this
    }

    /**
     * 设置错误图
     */
    fun error(resId: Int): BitmapLoad {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.errorDrawable = context.getDrawable(resId)
        }
        return this
    }

    /**
     * 设置错误图
     */
    fun error(drawable: Drawable): BitmapLoad {
        this.errorDrawable = drawable
        return this
    }

    /**
     * 设置自定义Bitmap处理器
     */
    fun processor(processor: BitmapProcessor): BitmapLoad {
        this.bitmapProcessor = processor
        return this
    }

    /**
     * 设置加载监听器
     */
    fun listener(listener: BitmapLoadListener): BitmapLoad {
        this.listener = listener
        return this
    }

    /**
     * 加载到指定View
     */
    fun into(view: View) {
        this.targetView = view
        startLoad()
    }

    /**
     * 取消加载
     */
    fun cancel() {
        isCancelled = true
    }

    /**
     * 开始加载图片
     */
    private fun startLoad() {
        if (source == null || targetView == null) {
            listener?.onError(Exception("Source or target view is null"))
            return
        }

        // 重置取消标志
        isCancelled = false

        // 显示占位图
        showPlaceholder()

        // 获取View尺寸
        val viewWidth = targetView!!.width
        val viewHeight = targetView!!.height

        // 如果View还没有测量完成，等待布局完成
        if (viewWidth <= 0 || viewHeight <= 0) {
            targetView!!.post {
                startLoad()
            }
            return
        }

        // 在后台线程加载图片
        executor.execute {
            try {
                if (isCancelled) return@execute

                val bitmap = loadBitmap(source!!, viewWidth, viewHeight)

                if (isCancelled || bitmap == null) return@execute

                // 切换到主线程显示图片
                mainHandler.post {
                    if (!isCancelled) {
                        displayBitmap(bitmap)
                        listener?.onSuccess(bitmap)
                    }
                }
            } catch (e: Exception) {
                if (isCancelled) return@execute

                // 切换到主线程显示错误
                mainHandler.post {
                    if (!isCancelled) {
                        showError()
                        listener?.onError(e)
                    }
                }
            }
        }
    }

    /**
     * 加载Bitmap
     */
    private fun loadBitmap(source: Any, targetWidth: Int, targetHeight: Int): Bitmap? {
        return when (source) {
            is Int -> loadFromResource(source, targetWidth, targetHeight)
            is String -> loadFromPath(source, targetWidth, targetHeight)
            else -> throw IllegalArgumentException("Unsupported source type: ${source}. Only resource ID (Int) and local path (String) are supported.")
        }
    }

    /**
     * 从资源加载
     */
    private fun loadFromResource(
        resId: Int,
        targetWidth: Int,
        targetHeight: Int
    ): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, resId, options)

        // 计算采样率
        val sampleSize =
            calculateSampleSize(options.outWidth, options.outHeight, targetWidth, targetHeight)

        // 重新解码
        val decodeOptions = BitmapFactory.Options()
        decodeOptions.inSampleSize = sampleSize
        decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeResource(context.resources, resId, decodeOptions)
            ?: throw IOException("Failed to decode resource: $resId")
    }

    /**
     * 从文件路径加载
     */
    private fun loadFromPath(path: String, targetWidth: Int, targetHeight: Int): Bitmap? {
        val file = File(path)
        if (!file.exists()) {
            throw IOException("File not found: $path")
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            BitmapFactory.decodeStream(fis, null, options)
        } catch (e: Exception) {

        } finally {
            fis?.close()
        }

        // 计算采样率
        val sampleSize =
            calculateSampleSize(options.outWidth, options.outHeight, targetWidth, targetHeight)

        // 重新解码
        val decodeOptions = BitmapFactory.Options()
        decodeOptions.inSampleSize = sampleSize
        decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
        var realFis: FileInputStream? = null
        try {
            realFis = FileInputStream(file)
            BitmapFactory.decodeStream(realFis, null, decodeOptions)
        } catch (e: Exception) {

        } finally {
            fis?.close()
        }
        return null
    }

    /**
     * 计算采样率
     */
    private fun calculateSampleSize(
        originalWidth: Int,
        originalHeight: Int,
        targetWidth: Int,
        targetHeight: Int
    ): Int {
        var sampleSize = 1
        while (originalWidth / sampleSize > targetWidth || originalHeight / sampleSize > targetHeight) {
            sampleSize *= 2
        }
        return sampleSize
    }

    /**
     * 显示占位图
     */
    private fun showPlaceholder() {
        val placeholder = placeholder ?: return
        when (targetView) {
            is ImageView -> (targetView as ImageView).setImageDrawable(placeholder)
            else -> targetView!!.background = placeholder
        }
    }

    /**
     * 显示错误图
     */
    private fun showError() {
        val drawable = placeholder ?: return
        when (targetView) {
            is ImageView -> (targetView as ImageView).setImageDrawable(drawable)
            else -> targetView!!.background = drawable
        }
    }

    /**
     * 显示Bitmap
     */
    private fun displayBitmap(bitmap: Bitmap) {
        val processedBitmap = processBitmap(bitmap)
        val drawable = BitmapDrawable(context.resources, processedBitmap)

        when (targetView) {
            is ImageView -> (targetView as ImageView).setImageDrawable(drawable)
            else -> targetView!!.background = drawable
        }
    }

    /**
     * 处理Bitmap
     */
    private fun processBitmap(bitmap: Bitmap): Bitmap {
        return bitmapProcessor.process(bitmap, targetView!!.width, targetView!!.height)
    }
}

/**
 * Bitmap加载监听器
 */
interface BitmapLoadListener {
    fun onSuccess(bitmap: Bitmap)
    fun onError(exception: Exception)
}
