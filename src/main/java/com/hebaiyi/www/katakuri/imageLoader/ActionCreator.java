package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hebaiyi.www.katakuri.disposer.CacheDisposer;
import com.hebaiyi.www.katakuri.disposer.LoadingDisposer;
import com.hebaiyi.www.katakuri.util.ViewUtil;

public class ActionCreator {

    private Dispatcher mDispatcher;
    private String mPath;
    private int mWidth;
    private int mHeight;
    private int mRes;
    private boolean throughCache = true;
    private Caramel.Filter mFilter;
    private boolean isResize;
    private CacheDisposer mCacheDisposer;
    private LoadingDisposer mLoadingDisposer;

    ActionCreator(String path, Dispatcher dispatcher) {
        mPath = path;
        // 初始化责任链执行者
       initChain();
        // 初始化调配者
        mDispatcher = dispatcher;
    }

    /**
     *  初始化责任链
     */
    private void initChain(){
        mCacheDisposer = new CacheDisposer(throughCache);
        mLoadingDisposer = new LoadingDisposer();
        mCacheDisposer.setNextDisposer(mLoadingDisposer);
    }

    /**
     * 重新设置图片大小
     */
    public ActionCreator resize(int width, int height) {
        isResize = true;
        mWidth = width;
        mHeight = height;
        return this;
    }

    public ActionCreator filter(Caramel.Filter filter) {
        mFilter = filter;
        return this;
    }

    public ActionCreator placeholder(int res) {
        if (res == 0) {
            throw new IllegalArgumentException("Placeholder image resource invalid.");
        }
        mRes = res;
        return this;
    }

    public ActionCreator throughCache(boolean throughCache) {
        this.throughCache = throughCache;
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
        // 拦截事件
        mCacheDisposer.sparkIntercept();
        // 获取bitmap
        Bitmap bm = mCacheDisposer.disposeRequest(mPath);
        if (bm != null && throughCache) {
            // 加载图片
            Caramel.setBitmap(imageView, bm, mFilter);
        } else {
            // 获取宽高信息
            if (!isResize) {
                mWidth = ViewUtil.getWidth(imageView);
                mHeight = ViewUtil.getHeight(imageView);
            }
            mLoadingDisposer.setDimension(mWidth,mHeight);
            // 创建任务
            ImageAction action =
                    new ImageAction(mFilter, mDispatcher.getTaskSemaphore(),
                            imageView, mPath, mWidth, mHeight, mDispatcher, mCacheDisposer);
            // 执行任务
            mDispatcher.performExecute(action);
        }
    }

    String getPath() {
        return mPath;
    }


}
