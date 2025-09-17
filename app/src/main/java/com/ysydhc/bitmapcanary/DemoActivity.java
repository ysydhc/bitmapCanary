package com.ysydhc.bitmapcanary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawableResource;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import java.security.MessageDigest;


public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        findViewById(R.id.iv_go2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DemoActivity.this, DemoActivity2.class));
            }
        });

        ImageView glideIv = findViewById(R.id.glide_iv);
        glideIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGif(glideIv);
            }
        });
        glideIv.post(new Runnable() {
            @Override
            public void run() {
                loadGif(glideIv);
            }
        });
        findViewById(R.id.nine_path2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.setVisibility(View.GONE);
                ViewParent parent = v.getParent();
                ((ViewGroup) parent).removeView(v);
            }
        });
    }

    private void loadGif(ImageView glideIv) {
//        glideIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        glideIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide glide = Glide.get(DemoActivity.this);
        Glide.with(DemoActivity.this.getApplicationContext())
//                .load("https://cdn.ekatox.com/simejiglobal/facemoji/stickertab/test/08791cb2-bf83-445b-9a81-e697338ae7fd.gif")
                .load("https://q5.itc.cn/images01/20250117/1526ba6231b44f269db0d33aa7c334fc.jpeg")
                .transform(new CenterCrop(glide.getBitmapPool()))
//                .transform(new FitCenter(glide.getBitmapPool()))
//                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                .override(400, 400)
//                .transform(new Transformation<Bitmap>() {
//                    @Override
//                    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
//                        Bitmap bitmap = resource.get();
//                        Bitmap result = BitmapUtil.scaleBitmap(bitmap, outWidth, outHeight, glideIv.getScaleType());
//                        return BitmapResource.obtain(result, glide.getBitmapPool());
//                    }
//
//                    @Override
//                    public String getId() {
//                        return "custom.Transformation";
//                    }
//                })
//                .transform(new FitCenter(glide.getBitmapPool()))
//                .transform(new BitmapTransformation(getApplicationContext()) {
//
//                    @Override
//                    public String getId() {
//                        return "custom.BitmapTransformation";
//                    }
//
//                    @Override
//                    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
//                        Bitmap bitmap = BitmapUtil.scaleBitmap(toTransform, outWidth, outHeight, glideIv.getScaleType());
//                        return bitmap;
//                    }
//                })
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        return false;
//                    }
//                })
//                .into(new SimpleTarget<GlideDrawable>() {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                        glideIv.setImageDrawable(resource);
//                        resource.start();
//                    }
//                });
//                .into(glideIv);
//                 .into(new DrawableImageViewTarget(glideIv));
//                .into(new GlideDrawableImageViewTarget(glideIv));
//                .into(new ImageViewTarget<Drawable>(glideIv) {
//                    @Override
//                    protected void setResource(Drawable resource) {
//                        glideIv.setImageDrawable(resource);
//                    }
//                });
                .into(new ImageViewTarget<GlideDrawable>(glideIv) {
                    @Override
                    protected void setResource(GlideDrawable resource) {
                        glideIv.setImageDrawable(resource);
                        resource.start();
                    }
                });
    }
}
