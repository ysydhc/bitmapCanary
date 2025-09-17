package com.ysydhc.bitmapcanary

import android.app.Activity
import android.app.Application
import android.content.Context
import com.ysydhc.bitmapcanary.hook.Helper
import com.ysydhc.bitmapcanary.watch.ActivityDrawableWatcher

object BitmapCanary {

    fun hook(context: Context) {
        install(context)
    }

    fun watch(application: Application) {
        ActivityDrawableWatcher.watchDrawable(application)
    }

    @Synchronized
    private fun install(context: Context) {
        Helper.parseMetaData(context)
        Helper.startHook()
    }

    /**
     * 忽略一个Activity的某些view
     * viewIds不传就是全部忽略
     */
    @Synchronized
    fun ignoreActivity(clazz: Class<out Activity>, viewIds: List<Int> = emptyList()) {
        ignoreClassWithIds(clazz.name, viewIds)
    }

    /**
     * 忽略一个类的某些View
     * viewIds不传就是全部忽略
     */
    @Synchronized
    fun ignoreClassWithIds(
        clazzName: String,
        viewIds: List<Int> = emptyList()
    ) {
        if (Helper.mContext == null) {
            return
        }

        val viewNames = viewIds.map { id ->
            Helper.getViewNameById(Helper.mContext!!.resources, id)
        }

        ignoreClassWithNames(clazzName, viewNames)
    }

    /**
     * 忽略一个类的某些View
     * viewNames不传就是全部忽略
     */
    @Synchronized
    fun ignoreClassWithNames(
        clazzName: String,
        viewNames: List<String> = emptyList()
    ) {
        if (Helper.mContext == null) {
            return
        }

        Helper.ignoreClassMap[clazzName] = viewNames
    }
}