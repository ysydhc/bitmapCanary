package com.ysydhc.bitmapcanary.watch

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.ysydhc.bitmapcanary.decoder.IDrawableDecoder
import com.ysydhc.bitmapcanary.TAG
import com.ysydhc.bitmapcanary.utils.DrawableDetectUtil
import java.lang.ref.WeakReference
import java.util.WeakHashMap
import kotlin.collections.set

class ActivityDrawableWatcher(private val application: Application) {

    // 用WeakHashMap防止View泄漏
    private val listenerRecord: WeakHashMap<View, LayoutChangeDetectListener> = WeakHashMap()

    private var isWatching = false

    companion object {
        @Volatile
        lateinit var watcher: ActivityDrawableWatcher

        @JvmStatic
        @Synchronized
        fun watchDrawable(application: Application) {
            if (!this::watcher.isInitialized) {
                watcher = ActivityDrawableWatcher(application)
            }
            DrawableDecoderFactory.registerDef()
            ActivityDrawableWatcher(application).startWatch()
        }

        @JvmStatic
        @Synchronized
        fun watchView(app: Application, view: View) {
            if (!this::watcher.isInitialized) {
                watcher = ActivityDrawableWatcher(app)
            }
            if (watcher.listenerRecord.contains(view)) {
                return
            }
            val drawableDetectListener = LayoutChangeDetectListener(view)
            view.viewTreeObserver.addOnGlobalLayoutListener(drawableDetectListener)
            watcher.listenerRecord[view] = drawableDetectListener
        }

        fun unWatchView(view: View) {
            if (!this::watcher.isInitialized) {
                return
            }
            val viewTreeObserver = view.viewTreeObserver
            watcher.listenerRecord.remove(view)?.let { listener ->
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnGlobalLayoutListener(listener)
                }
            }
        }

        fun registerDrawableDecoder(decoder: IDrawableDecoder<*>) {
            DrawableDecoderFactory.registerDrawableDecoder(decoder)
        }

        fun clearDrawableDecoder() {
            DrawableDecoderFactory.clearDrawableDecoder()
        }

        fun registerViewDetector(viewDetector: ViewDetector<*>) {
            DetectorFactory.registerCustomDetector(viewDetector)
        }
    }

    // 内部类，rootView用WeakReference
    private class LayoutChangeDetectListener(rootView: View?) : OnGlobalLayoutListener {
        private val rootViewRef = WeakReference<View>(rootView)
        override fun onGlobalLayout() {
            val view = rootViewRef.get()
            if (view is ViewGroup) {
                val start = SystemClock.currentThreadTimeMillis()
                DrawableDetectUtil.detectDrawableSize(view)
                val end = SystemClock.currentThreadTimeMillis()
                Log.d(TAG, "watch time: ${end - start}")
            }
        }
    }

    private val lifecycleCallbacks: ActivityLifecycleCallbacks =
        object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                val decorView = activity.window.decorView
                val drawableDetectListener = LayoutChangeDetectListener(decorView)
                decorView.viewTreeObserver.addOnGlobalLayoutListener(drawableDetectListener)
                listenerRecord[decorView] = drawableDetectListener
            }

            override fun onActivityDestroyed(activity: Activity) {
                val decor = activity.window.decorView
                val viewTreeObserver = decor.viewTreeObserver
                listenerRecord.remove(decor)?.let { listener ->
                    if (viewTreeObserver.isAlive) {
                        viewTreeObserver.removeOnGlobalLayoutListener(listener)
                    }
                }
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}


        }

    /**
     * 开始监听（幂等）
     */
    private fun startWatch() {
        if (isWatching) {
            return
        }
        isWatching = true
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    /**
     * 停止监听（幂等）
     */
    private fun stopWatch() {
        if (!isWatching) {
            return
        }
        isWatching = false
        application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
        listenerRecord.clear()
        clearDrawableDecoder()
    }
}