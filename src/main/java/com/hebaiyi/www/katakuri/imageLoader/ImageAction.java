package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hebaiyi.www.katakuri.disposer.CacheDisposer;

import java.util.concurrent.Semaphore;

public class ImageAction implements Runnable {

    private Dispatcher mDispatcher;
    private String mPath;
    private Bitmap mBitmap;
    private ImageView mImageView;
    private Semaphore mSemaphore;
    private Caramel.Filter mFilter;
    private CacheDisposer mDisposer;

    ImageAction(Caramel.Filter filter, Semaphore semaphore,
                ImageView imageView, String path,
                Dispatcher dispatcher, CacheDisposer disposer) {
        mPath = path;
        mDispatcher = dispatcher;
        mImageView = imageView;
        mSemaphore = semaphore;
        mFilter = filter;
        mDisposer = disposer;
    }

    @Override
    public void run() {
        // 设置不拦截
        mDisposer.refuseIntercept();
        // 执行任务
        mBitmap = mDisposer.disposeRequest(mPath);
        // 任务完成
        mDispatcher.performFinish(this);
        // 释放信号量
        mSemaphore.release();

    }

    /**
     * 获取对应任务的图片地址
     *
     * @return 图片地址
     */
    String getPath() {
        return mPath;
    }

    /**
     * 获取加载得到的bitmap
     *
     * @return 对应的bitmap对象
     */
    Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * 获取ImageView
     *
     * @return 对应的imageView
     */
    ImageView getImageView() {
        return mImageView;
    }

    /**
     * 获取过滤器
     *
     * @return 过滤器
     */
    Caramel.Filter getFilter() {
        return mFilter;
    }


}
