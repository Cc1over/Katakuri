package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryCache {

    private LruCache<String, Bitmap> mLruCache;

    private static class Singleton {
        private static MemoryCache instance = new MemoryCache();
    }

    /**
     *  获取内存储存容器
     * @return 内存储存对象
     */
    public static MemoryCache getInstance() {
        return Singleton.instance;
    }

    private MemoryCache() {
        // 获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 缓存区域的内存大小
        int cacheSize = maxMemory / 4;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    /**
     * 将图片资源加载到内存中
     *
     * @param imageUrl 图片链接
     * @param bitmap   对应图片的bitmap对象
     */
    public void addBitmapToCache(String imageUrl, Bitmap bitmap) {
        if (getBitmapFromCache(imageUrl) == null) {
            if (bitmap != null) {
                mLruCache.put(imageUrl, bitmap);
            }
        }
    }

    /**
     * 把图片资源从内存取出
     *
     * @param imageUrl 图片链接
     * @return 对应图片的bitmap对象
     */
    public Bitmap getBitmapFromCache(String imageUrl) {
        return mLruCache.get(imageUrl);
    }

}
