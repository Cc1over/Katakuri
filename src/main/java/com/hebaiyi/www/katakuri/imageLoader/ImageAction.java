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

    ImageAction(Semaphore semaphore, ImageView imageView, String path, int width, int height, MemoryCache cache, Dispatcher dispatcher) {
        mPath = path;
        mDispatcher = dispatcher;
        mCache = cache;
        mWidth = width;
        mHeight = height;
        mImageView = imageView;
        mSemaphore = semaphore;
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

    String getPath() {
        return mPath;
    }

    Bitmap getBitmap() {
        return mBitmap;
    }

    ImageView getImageView(){
        return mImageView;
    }


}
