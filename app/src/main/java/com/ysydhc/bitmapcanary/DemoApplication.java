package com.ysydhc.bitmapcanary;

import android.app.Application;

import com.ysydhc.bitmapcanary.watch.ActivityDrawableWatcher;
import com.ysydhc.glideext.GlideExt;


public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GlideExt.INSTANCE.register();
        ActivityDrawableWatcher.watchDrawable(this);
//        BitmapCanary.INSTANCE.hook(this);
    }
}
