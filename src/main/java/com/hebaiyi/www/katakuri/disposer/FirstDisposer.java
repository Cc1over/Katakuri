package com.hebaiyi.www.katakuri.disposer;

import android.graphics.Bitmap;

import com.hebaiyi.www.katakuri.imageLoader.MemoryCache;

public class FirstDisposer extends Disposer {

    // Bitmap对象
    private Bitmap mBitmap;
    // 是否经过缓存
    private boolean throughCache;
    // 内存缓存对象
    private MemoryCache mCache = MemoryCache.getInstance();
    // 下个执行者
    private SecondDisposer mNextDisposer;

    public FirstDisposer(boolean throughCache) {
        this.throughCache = throughCache;
        // 下一个执行者
        mNextDisposer = new SecondDisposer();
        // 设置下一个执行者
        setNextDisposer(mNextDisposer);
    }

    /**
     *  设置压缩尺寸
     * @param width 宽度
     * @param height 高度
     */
    public void setDimension(int width,int height){
        mNextDisposer.setDimension(width,height);
    }

    @Override
    protected Bitmap echo(String request) {
        return mBitmap;
    }

    @Override
    protected boolean canDisposeRequest(String request) {
        if (!throughCache) {
            return false;
        }
        mBitmap = mCache.getBitmapFromCache(request);
        return mBitmap != null;
    }



}
