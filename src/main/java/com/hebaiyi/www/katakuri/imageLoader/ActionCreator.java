package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ActionCreator {

    private MemoryCache mMemoryCache;
    private Dispatcher mDispatcher;
    private String mUri;
    private int mWidth;
    private int mHeight;

    ActionCreator(String uri, Dispatcher dispatcher) {
        mUri = uri;
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

    /**
     * 加载图片
     *
     * @param imageView 对应的ImageView对象
     */
    public void into(ImageView imageView) {
        // 给imageView设置标签
        imageView.setTag(mUri);
        // 从内存取出bitmap
        Bitmap bm = mMemoryCache.getBitmapFromCache(mUri);
        if (bm != null) {
            imageView.setImageBitmap(bm);
        } else {
            // 获取宽高信息
            mWidth = ImageUtil.getWidth(imageView);
            mHeight = ImageUtil.getHeight(imageView);
            // 创建任务
            ImageAction action = new ImageAction(mDispatcher.getTaskSemaphore(),imageView, mUri, mWidth, mHeight, mMemoryCache, mDispatcher);
            // 执行任务
            mDispatcher.performExecute(action);
        }
    }

}
