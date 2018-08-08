package com.hebaiyi.www.katakuri.disposer;

import android.graphics.Bitmap;

import com.hebaiyi.www.katakuri.imageLoader.BitmapCompress;
import com.hebaiyi.www.katakuri.util.MemoryCache;

public class LoadingDisposer extends Disposer {

    // 内存缓存类
    private MemoryCache mCache = MemoryCache.getInstance();
    // 宽高信息
    private int mWidth,mHeight;
    // 结果
    private Bitmap mBitmap;

    public LoadingDisposer(){

    }

    /**
     *  设置压缩尺寸
     * @param width 宽度
     * @param height 高度
     */
    public void setDimension(int width,int height){
        mWidth = width;
        mHeight = height;
    }

    @Override
    protected Bitmap echo(String request) {
        // 添加到内存中
        mCache.addBitmapToCache(request, mBitmap);
        return mBitmap;
    }

    @Override
    protected boolean canDisposeRequest(String request) {
        // 获取并且压缩图片
        mBitmap = BitmapCompress.sampleCompression(request,mWidth,mHeight);
        return mBitmap != null;
    }


}
