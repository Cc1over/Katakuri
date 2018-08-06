package com.hebaiyi.www.katakuri.util;

import java.util.LinkedList;
import java.util.Queue;

public abstract class ObjectPool<T> {

    // 对象池的大小
    private int mSize;
    // 对象池队列
    private Queue<T> mQueue;

    public ObjectPool(int size) {
        mSize = size;
        mQueue = new LinkedList<>();
    }

    /**
     * 获取相应对象
     *
     * @return 相应的对应
     */
    public T obtain() {
        if (mQueue.size() == 0) {
            return newInstance();
        } else {
            return resetInstance(mQueue.poll());
        }
    }

    /**
     * 向对象池添加对象
     *
     * @return 是否添加成功
     */
    public boolean revert(T obj) {
        if (obj != null && mQueue.size() < mSize) {
            return mQueue.offer(obj);
        }
        return false;
    }

    abstract protected T newInstance();

    abstract protected T resetInstance(T t);
}
