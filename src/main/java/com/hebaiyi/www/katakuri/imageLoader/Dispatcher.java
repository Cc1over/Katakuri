package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Dispatcher {

    private static final int FINISH_TASK = 121;
    private static final int EXECUTE_TASK = 220;
    private static final int CPU_CORE_NUM = CPUUtil.obtainCPUCoreNum();

    private static volatile Dispatcher instance;

    private DispatcherHandler mDispatcherHandler;
    private UIHandler mUIHandler;
    private MemoryCache mMemoryCache;
    private ExecutorService mTreadPool;
    private Type mType = Type.LIFO;
    private LinkedList<ImageAction> mTaskQueue;
    private Semaphore mTaskSemaphore;

    public static Dispatcher getInstance() {
        if (instance == null) {
            synchronized (instance) {
                if (instance == null) {
                    instance = new Dispatcher();
                }
            }
        }
        return instance;
    }

    private Dispatcher() {
        // 初始化后台轮循线程
        DispatcherThread backgroundThread = new DispatcherThread();
        // 启动轮循线程
        backgroundThread.start();
        // 初始化后台工作handler
        mDispatcherHandler = new DispatcherHandler(backgroundThread.getLooper(), this);
        // 创建内存缓存类
        mMemoryCache = MemoryCache.getInstance();
        // 创建线程池
        mTreadPool = Executors.newFixedThreadPool(CPU_CORE_NUM + 1);
        // 创建队列
        mTaskQueue = new LinkedList<>();
        // 初始化执行的信号量
        mTaskSemaphore = new Semaphore(CPUUtil.obtainCPUCoreNum() + 1);
    }

    /**
     * 供外界调用示意完成任务
     *
     * @param bm        对应的bitmap对象
     * @param uri       加载地址
     * @param imageView 相应的imageView对象
     */
    public void performFinish(Bitmap bm, String uri, ImageView imageView) {
        Message message = Message.obtain();
        message.what = FINISH_TASK;
        ImageHolder holder = new ImageHolder();
        holder.bitmap = bm;
        holder.uri = uri;
        holder.imageView = imageView;
        message.obj = holder;
        mDispatcherHandler.sendMessage(message);
    }

    /**
     * 加载图片
     *
     * @param uri       对应本地地址
     * @param imageView 对应的imageView
     */
    public void performLoad(final String uri, final ImageView imageView) {
        imageView.setTag(uri);
        if (mUIHandler == null) {
            mUIHandler = new UIHandler();
        }
        Bitmap bm = mMemoryCache.getBitmapFromCache(uri);
        if (bm != null) {
            // 刷新bitmap
            freshBitmap(bm, uri, imageView);
        } else {
            // 添加进队列并执行
            ImageAction action = new ImageAction(uri,imageView,mMemoryCache,mTaskSemaphore);
            addTask(action);
        }
    }

    /**
     * 把任务添加到队列中
     *
     * @param action 加载任务
     */
    private void addTask(ImageAction action) {
        mTaskQueue.add(action);
        mDispatcherHandler.sendEmptyMessage(EXECUTE_TASK);
    }

    /**
     * 获取任务
     *
     * @return 加载任务
     */
    private ImageAction getTask() {
        if (mType == Type.LIFO) {
            return mTaskQueue.removeFirst();
        } else {
            return mTaskQueue.removeLast();
        }
    }

    /**
     * 执行加载任务
     */
    private void executeTask() {
        mTreadPool.execute(getTask());
        try {
            mTaskSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载任务完成
     */
    private void finish(Message message) {
        mUIHandler.sendMessage(message);
    }

    /**
     * 内部调用的加载完成方法
     */
    private void freshBitmap(Bitmap bm, String uri, ImageView imageView) {
        Message message = Message.obtain();
        ImageHolder holder = new ImageHolder();
        holder.bitmap = bm;
        holder.uri = uri;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }

    private static class DispatcherHandler extends Handler {

        private final WeakReference<Dispatcher> mDispatcher;

        DispatcherHandler(Looper looper, Dispatcher dispatcher) {
            super(looper);
            mDispatcher = new WeakReference<>(dispatcher);
        }

        @Override
        public void handleMessage(Message msg) {
            Dispatcher dispatcher = mDispatcher.get();
            super.handleMessage(msg);
            switch (msg.what) {
                case FINISH_TASK:
                    // 完成加载任务
                    dispatcher.finish(msg);
                    break;
                case EXECUTE_TASK:
                    // 添加进队列执行加载任务
                    dispatcher.executeTask();
                    break;
            }
        }
    }

    public enum Type {
        FIFO, LIFO
    }

    private class ImageHolder {
        String uri;
        ImageView imageView;
        Bitmap bitmap;
    }


    private static class DispatcherThread extends HandlerThread {

        DispatcherThread() {
            super("dispatcher-thread");
        }

    }

    private static class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 获取设置图片所需的参数
            ImageHolder holder = (ImageHolder) msg.obj;
            Bitmap bm = holder.bitmap;
            ImageView imageView = holder.imageView;
            String uri = holder.uri;
            // 设置图片
            if (imageView.getTag().equals(uri)) {
                imageView.setImageBitmap(bm);
            }
        }
    }


}
