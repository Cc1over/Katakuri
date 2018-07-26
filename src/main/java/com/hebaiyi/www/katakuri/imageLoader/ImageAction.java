package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;

public class ImageAction implements Runnable {

    private Dispatcher mDispatcher;
    private String mUri;
    private int mWidth;
    private int mHeight;
    private Bitmap mBitmap;
    private MemoryCache mCache;

    ImageAction(String uri, int width,int height, MemoryCache cache,Dispatcher dispatcher) {
        mUri = uri;
        mDispatcher = dispatcher;
        mCache = cache;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void run() {
        // 获取并且压缩图片
        mBitmap = BitmapCompress.sampleCompression(mUri, mWidth, mHeight);
        // 添加到内存中
        mCache.addBitmapToCache(mUri, mBitmap);
        // 任务完成
        mDispatcher.performFinish(this);
    }

    String getUri(){
        return mUri;
    }

    Bitmap getBitmap(){
        return mBitmap;
    }

}
