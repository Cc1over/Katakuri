package com.hebaiyi.www.katakuri.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

public class MatrixImageView extends android.support.v7.widget.AppCompatImageView
        implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private static final int DELAY_MILLIS = 15;

    private float mMaxScale; // 最大缩放值
    private float mMidScale; // 双击缩放值
    private float mInitScale; // 初始化缩放值
    private Matrix mMatrix;
    private int mTouchSlop;
    private ScaleGestureDetector mScaleGesture;
    private GestureDetector mGestureDetector;
    private boolean isFirst = true;
    private boolean isScaling;
    private float mLastX;
    private float mLastY;
    private boolean isCanDrag;
    private int lastPointerCount;

    public MatrixImageView(Context context) {
        super(context);
        init(context);
    }


    public MatrixImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MatrixImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        // 获取系统最小滑动距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScaleGesture = new ScaleGestureDetector(context, this);
        // 初始化手势双击监听
        initDoubleGestureDetector(context);
        setOnTouchListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }


    @Override
    public void onGlobalLayout() {
        if (isFirst) {
            int width = getWidth();
            int height = getHeight();
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            float scale = 1.0f;
            if (width < intrinsicWidth && height > intrinsicHeight) {
                scale = width * 1.0f / intrinsicWidth;
            }
            if (width > intrinsicWidth && height < intrinsicHeight) {
                scale = height * 1.0f / intrinsicHeight;
            }
            if ((width < intrinsicWidth && height < intrinsicHeight) || (width > intrinsicWidth && height > intrinsicHeight)) {
                scale = Math.min(width * 1.0f / intrinsicWidth, height * 1.0f / intrinsicHeight);
            }
            // 初始化缩放数据
            mInitScale = scale;
            mMidScale = 2 * mInitScale;
            mMaxScale = 4 * mInitScale;
            //将图片移动到控件的中心
            float dx = width / 2 - intrinsicWidth / 2;
            float dy = height / 2 - intrinsicHeight / 2;
            mMatrix.postTranslate(dx, dy);
            mMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
            setImageMatrix(mMatrix);
            isFirst = false;
        }
    }

    /**
     * 初始化手势双击监听
     */
    private void initDoubleGestureDetector(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isScaling) {
                    return true;
                }
                //以此点为缩放中心
                float x = e.getX();
                float y = e.getY();

                if (getScale() < mMidScale) {
                    postDelayed(new AutoScaleRunnable(mMidScale, x, y), DELAY_MILLIS);
                    isScaling = true;
                } else {
                    postDelayed(new AutoScaleRunnable(mInitScale, x, y), DELAY_MILLIS);
                    isScaling = true;
                }
                return true;
            }
        });
    }

    /**
     * 获取缩放后的宽高以及坐标点
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mMatrix;
        RectF rect = new RectF();
        Drawable drawable = getDrawable();
        if (null != drawable) {
            rect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 获取当前缩放值
     */
    private float getScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null) {
            return true;
        }
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }
            mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);
        }

        return true;
    }

    /**
     * 检测和测试白边的位置
     */
    private void checkBorderAndCenterWhenScale() {
        // 获取当前图片的顶点位置
        RectF rectF = getMatrixRectF();
        // 偏移量
        float deltaX = 0.0f;
        float deltaY = 0.0f;
        // 获取图片的宽高
        int width = getWidth();
        int height = getHeight();
        // 图片高度大于控件高度
        if (rectF.height() >= height) {
            // 图片顶部出现空白
            if (rectF.top > 0) {
                // 往上移动
                deltaY = -rectF.top;
            }
            // 图片底部出现空白
            if (rectF.bottom < height) {
                // 往下移动
                deltaY = height - rectF.bottom;
            }
        }
        // 图片宽度大于控件宽度
        if (rectF.width() >= width) {
            // 图片左边出现空白
            if (rectF.left > 0) {
                // 往左边移动
                deltaX = -rectF.left;
            }
            // 图片右边出现空白
            if (rectF.right < width) {
                // 往右边移动
                deltaX = width - rectF.right;
            }
        }
        // 控件宽度大于图片宽度
        if (rectF.width() < width) {
            deltaX = width / 2 - rectF.right + rectF.width() / 2;
        }
        // 控件高度大于图片高度
        if (rectF.height() < height) {
            deltaY = height / 2 - rectF.bottom + rectF.height() / 2;
        }
        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        mScaleGesture.onTouchEvent(event);

        float x = 0;
        float y = 0;
        final int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        lastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if ((rectF.width() - getWidth() > 5 || rectF.height() - getHeight() > 5)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if ((rectF.width() - getWidth() > 5 || rectF.height() - getHeight() > 5)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {
                        if (rectF.width() < getWidth()) {
                            dx = 0;
                        }
                        if (rectF.height() < getHeight()) {
                            dy = 0;
                        }
                        mMatrix.postTranslate(dx, dy);
                        checkBorderAndCenterWhenScale();
                        setImageMatrix(mMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                lastPointerCount = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }
        return true;
    }

    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    private class AutoScaleRunnable implements Runnable {

        private float mTargetScale;
        private float tmpScale;
        // 缩放中心
        private float x;
        private float y;
        //放大与缩小的梯度
        private final float BIG = 1.07F;
        private final float SMALL = 0.97F;

        AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIG;
            }
            if (getScale() > mTargetScale) {
                tmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            mMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);
            float currentScale = getScale();
            if ((tmpScale > 1.0f && currentScale < mTargetScale)
                    || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, DELAY_MILLIS);
            } else {
                isScaling = false;
                //到达了目标值
                float scale = mTargetScale / currentScale;
                mMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mMatrix);
            }
        }
    }

}
