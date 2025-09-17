package com.ysydhc.bitmapcanary;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Bitmap工具类，提供图片缩放、压缩、旋转等功能
 */
public class BitmapUtil {

    private static final String TAG = "BitmapUtil";

    /**
     * 根据指定尺寸缩放图片
     *
     * @param bitmap       原始图片
     * @param targetWidth  目标宽度
     * @param targetHeight 目标高度
     * @param scaleType    缩放模式
     * @return 缩放后的图片
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int targetWidth, int targetHeight, ImageView.ScaleType scaleType) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.w(TAG, "bitmap is null or recycled");
            return null;
        }

        if (targetWidth <= 0 || targetHeight <= 0) {
            Log.w(TAG, "target size is invalid");
            return bitmap;
        }

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        // 如果尺寸相同，直接返回原图
        if (originalWidth == targetWidth && originalHeight == targetHeight) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        float scaleX, scaleY;

        switch (scaleType) {
            case CENTER_CROP:
                scaleX = Math.max((float) targetWidth / originalWidth, (float) targetHeight / originalHeight);
                scaleY = scaleX;
                break;
            case FIT_CENTER:
            case CENTER_INSIDE:
                scaleX = Math.min((float) targetWidth / originalWidth, (float) targetHeight / originalHeight);
                scaleY = scaleX;
                break;
            case FIT_XY:
                scaleX = (float) targetWidth / originalWidth;
                scaleY = (float) targetHeight / originalHeight;
                break;
            default:
                scaleX = scaleY = 1.0f;
        }

        matrix.setScale(scaleX, scaleY);

        // 计算居中偏移
        float offsetX = 0, offsetY = 0;
        if (scaleType == ImageView.ScaleType.CENTER_CROP || scaleType == ImageView.ScaleType.CENTER_INSIDE) {
            float scaledWidth = originalWidth * scaleX;
            float scaledHeight = originalHeight * scaleY;
            offsetX = (targetWidth - scaledWidth) / 2;
            offsetY = (targetHeight - scaledHeight) / 2;
        }

        matrix.postTranslate(offsetX, offsetY);

        try {
            Bitmap scaledBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                    bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.drawBitmap(bitmap, matrix, new Paint(Paint.ANTI_ALIAS_FLAG));
            return scaledBitmap;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError when scaling bitmap", e);
            return bitmap;
        }
    }

    /**
     * 按比例缩放图片
     *
     * @param bitmap 原始图片
     * @param scale  缩放比例 (0.1f - 2.0f)
     * @return 缩放后的图片
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.w(TAG, "bitmap is null or recycled");
            return null;
        }

        if (scale <= 0) {
            Log.w(TAG, "scale factor is invalid: " + scale);
            return bitmap;
        }

        int newWidth = Math.round(bitmap.getWidth() * scale);
        int newHeight = Math.round(bitmap.getHeight() * scale);

        return scaleBitmap(bitmap, newWidth, newHeight, ImageView.ScaleType.FIT_CENTER);
    }

    /**
     * 按最大尺寸缩放图片（保持宽高比）
     *
     * @param bitmap  原始图片
     * @param maxSize 最大尺寸（像素）
     * @return 缩放后的图片
     */
    public static Bitmap scaleBitmapToMaxSize(Bitmap bitmap, int maxSize) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.w(TAG, "bitmap is null or recycled");
            return null;
        }

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        if (originalWidth <= maxSize && originalHeight <= maxSize) {
            return bitmap;
        }

        float scale = Math.min((float) maxSize / originalWidth, (float) maxSize / originalHeight);
        return scaleBitmap(bitmap, scale);
    }

    /**
     * 按最小尺寸缩放图片（保持宽高比）
     *
     * @param bitmap  原始图片
     * @param minSize 最小尺寸（像素）
     * @return 缩放后的图片
     */
    public static Bitmap scaleBitmapToMinSize(Bitmap bitmap, int minSize) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.w(TAG, "bitmap is null or recycled");
            return null;
        }

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        if (originalWidth >= minSize && originalHeight >= minSize) {
            return bitmap;
        }

        float scale = Math.max((float) minSize / originalWidth, (float) minSize / originalHeight);
        return scaleBitmap(bitmap, scale);
    }

    /**
     * 裁剪图片
     *
     * @param bitmap 原始图片
     * @param x      裁剪起始X坐标
     * @param y      裁剪起始Y坐标
     * @param width  裁剪宽度
     * @param height 裁剪高度
     * @return 裁剪后的图片
     */
    public static Bitmap cropBitmap(Bitmap bitmap, int x, int y, int width, int height) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.w(TAG, "bitmap is null or recycled");
            return null;
        }

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        // 确保裁剪区域在图片范围内
        x = Math.max(0, Math.min(x, originalWidth));
        y = Math.max(0, Math.min(y, originalHeight));
        width = Math.min(width, originalWidth - x);
        height = Math.min(height, originalHeight - y);

        if (width <= 0 || height <= 0) {
            Log.w(TAG, "crop area is invalid");
            return bitmap;
        }

        try {
            return Bitmap.createBitmap(bitmap, x, y, width, height);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError when cropping bitmap", e);
            return bitmap;
        }
    }

    /**
     * 旋转图片
     *
     * @param bitmap  原始图片
     * @param degrees 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.w(TAG, "bitmap is null or recycled");
            return null;
        }

        if (degrees == 0) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);

        try {
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError when rotating bitmap", e);
            return bitmap;
        }
    }

    /**
     * 根据EXIF信息自动旋转图片
     *
     * @param bitmap    原始图片
     * @param imagePath 图片路径
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByExif(Bitmap bitmap, String imagePath) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            float degrees = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
                    break;
            }

            if (degrees != 0) {
                return rotateBitmap(bitmap, degrees);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading EXIF data", e);
        }

        return bitmap;
    }

    /**
     * 压缩图片质量
     *
     * @param bitmap  原始图片
     * @param quality 压缩质量 (0-100)
     * @return 压缩后的字节数组
     */
    public static byte[] compressBitmap(Bitmap bitmap, int quality) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        quality = Math.max(0, Math.min(100, quality));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    /**
     * 保存图片到文件
     *
     * @param bitmap   要保存的图片
     * @param filePath 保存路径
     * @param quality  压缩质量 (0-100)
     * @return 是否保存成功
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, String filePath, int quality) {
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }

        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.close();
            return success;
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap to file", e);
            return false;
        }
    }

    /**
     * 获取图片内存大小（字节）
     *
     * @param bitmap 图片
     * @return 内存大小
     */
    public static long getBitmapSize(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return 0;
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 安全回收Bitmap
     *
     * @param bitmap 要回收的Bitmap
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}

