package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.concurrent.Semaphore;

public class ImageAction implements Runnable {

    private Dispatcher mDispatcher;
    private String mPath;
    private int mWidth;
    private int mHeight;
    private Bitmap mBitmap;
    private MemoryCache mCache;
    private ImageView mImageView;
    private Semaphore mSemaphore;
    private Caramel.Filter mFilter;

    ImageAction(Caramel.Filter filter,Semaphore semaphore, ImageView imageView, String path, int width, int height, MemoryCache cache, Dispatcher dispatcher) {
        mPath = path;
        mDispatcher = dispatcher;
        mCache = cache;
        mWidth = width;
        mHeight = height;
        mImageView = imageView;
        mSemaphore = semaphore;
        mFilter = filter;
    }

    @Override
    public void run() {
        // 获取并且压缩图片
        mBitmap = BitmapCompress.sampleCompression(mPath, mWidth, mHeight);
        // 添加到内存中
        mCache.addBitmapToCache(mPath, mBitmap);
        // 任务完成
        mDispatcher.performFinish(this);
        // 释放信号量
        mSemaphore.release();
    }

    /**
     *  获取对应任务的图片地址
     * @return 图片地址
     */
    String getPath() {
        return mPath;
    }

    /**
     *  获取加载得到的bitmap
     * @return 对应的bitmap对象
     */
    Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     *  获取ImageView
     * @return 对应的imageView
     */
    ImageView getImageView(){
        return mImageView;
    }

    /**
     *  获取过滤器
     * @return 过滤器
     */
    Caramel.Filter getFilter(){
        return mFilter;
    }


}
