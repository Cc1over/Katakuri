package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ActionCreator {

    private MemoryCache mMemoryCache;
    private Dispatcher mDispatcher;
    private String mPath;
    private int mWidth;
    private int mHeight;
    private int mRes;

    ActionCreator(String path, Dispatcher dispatcher) {
        mPath = path;
        // 创建内存缓存类
        mMemoryCache = MemoryCache.getInstance();
        // 初始化调配者
        mDispatcher = dispatcher;
    }

    /**
     * 重新设置图片大小
     */
    public ActionCreator resize(int width, int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    public ActionCreator placeholder(int res) {
        if (res == 0) {
            throw new IllegalArgumentException("Placeholder image resource invalid.");
        }
        mRes = res;
        return this;
    }


    /**
     * 加载图片
     *
     * @param imageView 对应的ImageView对象
     */
    public void into(ImageView imageView) {
        // 设置占位图
        if (mRes != 0) {
            imageView.setImageResource(mRes);
        }
        // 给imageView设置标签
        imageView.setTag(mPath);
        // 从内存取出bitmap
        Bitmap bm = mMemoryCache.getBitmapFromCache(mPath);
        if (bm != null) {
            imageView.setImageBitmap(bm);
        } else {
            // 获取宽高信息
            mWidth = ImageUtil.getWidth(imageView);
            mHeight = ImageUtil.getHeight(imageView);
            // 创建任务
            ImageAction action = new ImageAction(mDispatcher.getTaskSemaphore(),
                    imageView, mPath, mWidth, mHeight, mMemoryCache, mDispatcher);
            // 执行任务
            mDispatcher.performExecute(action);
        }
    }

}
