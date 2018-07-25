package com.hebaiyi.www.katakuri.engine.InnerImageEngine;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.hebaiyi.www.katakuri.util.BitmapCompress;
import com.hebaiyi.www.katakuri.util.CPUUtil;
import com.hebaiyi.www.katakuri.util.ImageAware;
import com.hebaiyi.www.katakuri.util.MemoryCache;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ImageLoader {

    private static final int ADD_TASK = 0X14154;
    private volatile static ImageLoader instance;
    private MemoryCache mMemoryCache;
    private ExecutorService mTreadPool;
    private Type mType = Type.LIFO;
    private LinkedList<Runnable> mTaskQueue;
    private PoolThreadHandler mPoolThreadHandler;
    private UIHandler mUIHandler;
    private Semaphore mHandlerSemaphore = new Semaphore(0);
    private Semaphore mTaskSemaphore;

    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (instance) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    private ImageLoader() {
        // 初始化
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 初始化后台线程以及handler
        Thread poolThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                mPoolThreadHandler = new PoolThreadHandler(ImageLoader.this);
                mHandlerSemaphore.release();
                Looper.loop();
            }
        };
        poolThread.start();
        // 创建内存缓存类
        mMemoryCache = new MemoryCache();
        // 创建线程池
        mTreadPool = Executors.newFixedThreadPool(CPUUtil.obtainCPUCoreNum() + 1);
        // 创建队列
        mTaskQueue = new LinkedList<>();
        // 初始化执行的信号量
        mTaskSemaphore = new Semaphore(CPUUtil.obtainCPUCoreNum() + 1);
    }

    /**
     * 加载图片
     *
     * @param uri       对应本地uri
     * @param imageView 对应的imageView
     */
    public void loadImage(final String uri, final ImageView imageView) {
        imageView.setTag(uri);
        if (mUIHandler == null) {
            mUIHandler = new UIHandler();
        }
        Bitmap bm = mMemoryCache.getBitmapFromCache(uri);
        if (bm != null) {
            refreshBitmap(bm, uri, imageView);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    ImageAware imageAware = new ImageAware(imageView);
                    Bitmap bm = BitmapCompress.sampleCompression(uri, imageAware.getWidth(), imageAware.getHeight());
                    mMemoryCache.addBitmapToCache(uri, bm);
                    refreshBitmap(bm, uri, imageView);
                    mTaskSemaphore.release();
                }
            });
        }
    }

    private void refreshBitmap(Bitmap bm, String uri, ImageView imageView) {
        Message message = Message.obtain();
        ImageHolder holder = new ImageHolder();
        holder.bitmap = bm;
        holder.uri = uri;
        holder.imageView = imageView;
        message.obj = holder;

        mUIHandler.sendMessage(message);
    }

    private synchronized void addTask(Runnable runnable) {
        try {
            if (mPoolThreadHandler == null) {
                mHandlerSemaphore.acquire();
            }
            mTaskQueue.add(runnable);
            mPoolThreadHandler.sendEmptyMessage(ADD_TASK);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ImageHolder {
        Bitmap bitmap;
        ImageView imageView;
        String uri;
    }

    private Runnable getTask() {
        if (mType == Type.LIFO) {
            return mTaskQueue.removeFirst();
        } else {
            return mTaskQueue.removeLast();
        }
    }

    public enum Type {
        FIFO, LIFO
    }

    private static class PoolThreadHandler extends Handler {

        private final WeakReference<ImageLoader> mLoader;

        public PoolThreadHandler(ImageLoader loader) {
            mLoader = new WeakReference<>(loader);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageLoader loader = mLoader.get();
            loader.mTreadPool.execute(loader.getTask());
            try {
                loader.mTaskSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageHolder holder = (ImageHolder) msg.obj;
            Bitmap bm = holder.bitmap;
            ImageView imageView = holder.imageView;
            String uri = holder.uri;
            if (imageView.getTag().equals(uri)) {
                imageView.setImageBitmap(bm);
            }
        }
    }


}
