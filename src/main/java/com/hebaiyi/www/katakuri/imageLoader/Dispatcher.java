package com.hebaiyi.www.katakuri.imageLoader;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.hebaiyi.www.katakuri.util.CPUUtil;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Dispatcher {

    private static final int EXECUTE_TASK = 220;
    private static final int CPU_CORE_NUM = CPUUtil.obtainCPUCoreNum();

    private DispatcherHandler mDispatcherHandler;
    private Handler mUIHandler;
    private ExecutorService mTreadPool;
    private Type mType;
    private volatile List<Runnable> mTaskQueue;
    private Semaphore mTaskSemaphore;

    Dispatcher(Handler handler, Type type) {
        // 初始化后台轮循线程
        DispatcherThread backgroundThread = new DispatcherThread();
        // 启动轮循线程
        backgroundThread.start();
        // 初始化后台工作handler
        mDispatcherHandler = new DispatcherHandler(backgroundThread.getLooper(), this);
        // 创建线程池
        mTreadPool = Executors.newFixedThreadPool(CPU_CORE_NUM + 1);
        // 创建队列
        mTaskQueue = Collections.synchronizedList(new LinkedList<Runnable>());
        // 初始化执行的信号量
        mTaskSemaphore = new Semaphore(CPU_CORE_NUM + 1);
        // 缓存UIHandler
        mUIHandler = handler;
        // 设置调度方式
        mType = type;
    }

    /**
     * 供外界调用与任务执行完成
     *
     * @param action 对应的任务
     */
    public void performFinish(Runnable action) {
        Message message = Message.obtain();
        message.obj = action;
        mUIHandler.sendMessage(message);
    }

    /**
     * 把任务添加到队列中
     *
     * @param action 加载任务
     */
    public void performExecute(Runnable action) {
        mTaskQueue.add(action);
        mDispatcherHandler.sendEmptyMessage(EXECUTE_TASK);
    }

    /**
     * 获取任务
     *
     * @return 加载任务
     */
    private Runnable getTask() {
        if (mType == Type.LIFO) {
            return mTaskQueue.remove(mTaskQueue.size() - 1);
        }
        if (mType == Type.FIFO) {
            return mTaskQueue.remove(0);
        } else {
            throw new IllegalStateException("not exact method of scheduling");
        }
    }

    public Semaphore getTaskSemaphore() {
        return mTaskSemaphore;
    }

    /**
     * 执行加载任务
     */
    private void executeTask() {
        Runnable action = getTask();
        if (action == null) {
            return;
        }
        mTreadPool.execute(action);
        try {
            mTaskSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            if (dispatcher == null) {
                return;
            }
            super.handleMessage(msg);
            switch (msg.what) {
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

    private static class DispatcherThread extends HandlerThread {

        DispatcherThread() {
            super("dispatcher-thread");
        }

    }

}
