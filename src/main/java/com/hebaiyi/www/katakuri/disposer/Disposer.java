package com.hebaiyi.www.katakuri.disposer;

import android.graphics.Bitmap;

public abstract class Disposer {

    // 下一个处理者
    private Disposer mNextDisposer;
    // 是否拦截
    private boolean isIntercept;

    /**
     * 处理请求（模版方法）
     */
    public final Bitmap disposeRequest(String request) {
        Bitmap response;
        if (canDisposeRequest(request)) {
            response = this.echo(request);
        } else {
            //判断是否有下一个处理者
            if (!isIntercept && this.mNextDisposer != null) {
                response = this.mNextDisposer.disposeRequest(request);
            } else {
                // 请求无法处理，返回空结果
                response = null;
            }
        }
        return response;
    }

    /**
     *  外界选择拦截
     */
    public void sparkIntercept() {
        isIntercept = true;
    }

    public void refuseIntercept(){
        isIntercept = false;
    }

    public void setNextDisposer(Disposer disposer) {
        mNextDisposer = disposer;
    }

    /**
     * 处理消息的实现，每个子类实现
     *
     * @param request 请求
     * @return 处理结果
     */
    protected abstract Bitmap echo(String request);

    /**
     * 判断该请求处理者是否能处理这个请求
     *
     * @param request 请求
     * @return 是否能处理
     */
    protected abstract boolean canDisposeRequest(String request);

}
