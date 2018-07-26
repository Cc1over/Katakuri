package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.concurrent.Semaphore;

public class ImageAction implements Runnable {

    private Dispatcher mDispatcher;
    private String mUri;
    private ImageView mImageView;
    private MemoryCache mCache;
    private Semaphore mSemaphore;

    ImageAction(String uri, ImageView imageView, MemoryCache cache, Semaphore semaphore) {
        mImageView = imageView;
        mUri = uri;
        mDispatcher = Dispatcher.getInstance();
        mCache = cache;
        mSemaphore = semaphore;
    }

    @Override
    public void run() {
        // 获取宽高信息
        int width = ImageUtil.getWidth(mImageView);
        int height = ImageUtil.getHeight(mImageView);
        // 获取并且压缩图片
        Bitmap bm = BitmapCompress.sampleCompression(mUri, width, height);
        // 添加到内存中
        mCache.addBitmapToCache(mUri,bm);
        // 分派任务
        mDispatcher.performFinish(bm, mUri, mImageView);
        // 释放信号量
        mSemaphore.release();
    }

}
