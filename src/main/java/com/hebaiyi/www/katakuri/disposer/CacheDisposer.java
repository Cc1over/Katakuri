package com.hebaiyi.www.katakuri.disposer;

import android.graphics.Bitmap;
import android.util.Log;

import com.hebaiyi.www.katakuri.util.MemoryCache;

public class CacheDisposer extends Disposer {

    // Bitmap对象
    private Bitmap mBitmap;
    // 是否经过缓存
    private boolean throughCache;
    // 内存缓存对象
    private MemoryCache mCache = MemoryCache.getInstance();

    public CacheDisposer(boolean throughCache) {
        this.throughCache = throughCache;
    }

    @Override
    protected Bitmap echo(String request) {
        return mBitmap;
    }

    @Override
    protected boolean canDisposeRequest(String request) {
        Log.d("canDisposeRequest: ",throughCache+"");
        if (!throughCache) {
            return false;
        }
        mBitmap = mCache.getBitmapFromCache(request);
        return mBitmap != null;
    }



}
