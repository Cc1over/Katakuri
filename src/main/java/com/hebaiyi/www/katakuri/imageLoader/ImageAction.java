package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hebaiyi.www.katakuri.disposer.Disposer;
import com.hebaiyi.www.katakuri.disposer.FirstDisposer;

import java.util.concurrent.Semaphore;

public class ImageAction implements Runnable {

    private Dispatcher mDispatcher;
    private String mPath;
    private int mWidth;
    private int mHeight;
    private Bitmap mBitmap;
    private ImageView mImageView;
    private Semaphore mSemaphore;
    private Caramel.Filter mFilter;
    private FirstDisposer mDisposer;

    ImageAction(Caramel.Filter filter, Semaphore semaphore,
                ImageView imageView, String path, int width,
                int height, Dispatcher dispatcher, FirstDisposer disposer) {
        mPath = path;
        mDispatcher = dispatcher;
        mWidth = width;
        mHeight = height;
        mImageView = imageView;
        mSemaphore = semaphore;
        mFilter = filter;
        mDisposer = disposer;
    }

    @Override
    public void run() {
        // 设置宽高信息
        mDisposer.setDimension(mWidth, mHeight);
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
