package com.hebaiyi.www.katakuri.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.hebaiyi.www.katakuri.util.ObjectPool;

public class ZoomImageView extends android.support.v7.widget.AppCompatImageView
        implements ViewTreeObserver.OnGlobalLayoutListener {

    // 图片最大放大比例
    private static final float MAX_SCALE = 4.0f;
    // 惯性动画衰减参数
    private static final float FLING_DAMPING_FACTOR = 0.9f;
    // 矩阵池的大小
    private static final int OBJECT_POOL_SIZE = 16;
    // 没有手指触碰状态
    private static final int ZOOM_MODE_FREE = 0;
    // 单指滑动状态
    private static final int ZOOM_MODE_SCROLL = 1;
    // 双指拉伸状态
    private static final int ZOOM_MODE_SCALE = 2;
    // 缩放动画时长
    public static final int SCALE_ANIMATOR_DURATION = 200;
    // 外层变换矩阵，记录图片手势操作的最终结果
    private Matrix mOuterMatrix = new Matrix();
    // 内部矩阵，初始化时保存
    private Matrix mInnerMatrix = new Matrix();
    // 手势对象
    private GestureDetector mGestureDetector;
    // 单击监听对象
    private OnZoomClickListener mClickListener;
    // 矩阵对象池
    private ObjectPool<Matrix> mMatrixPool;
    // 矩形对象池
    private ObjectPool<RectF> mRectFPool;
    // 图片缩放动画对象
    private ScaleAnimation mScaleAnimation;
    // 滑动惯性动画对象
    private FlingAnimation mFlingAnimation;
    // 初始化标记
    private boolean isFirst = true;
    // 当前多指触碰的状态
    private int mCurrMode = ZOOM_MODE_FREE;
    // 上一次移动的位置
    private PointF mLastMovePoint = new PointF();
    // 缩放基准，以该缩放值为基本去进行缩放
    private float mScaleBase = 0;
    // 缩放中心
    private PointF mScaleCenter = new PointF();


    public ZoomImageView(Context context) {
        super(context);
        // 初始化
        init();
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 初始化
        init();
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 强制修改scaleType
        super.setScaleType(ScaleType.MATRIX);
        // 初始化矩阵对象池
        initMatrixPool();
        // 初始化矩形对象池
        initRectFPool();
        // 初始化手势对象
        initGestureDetector();
    }

    /**
     * 初始化矩形对象池
     */
    private void initRectFPool() {
        mRectFPool = new ObjectPool<RectF>(OBJECT_POOL_SIZE) {
            @Override
            protected RectF newInstance() {
                return new RectF();
            }

            @Override
            protected RectF resetInstance(RectF rectF) {
                rectF.setEmpty();
                return rectF;
            }
        };
    }

    /**
     * 初始化矩阵对象池
     */
    private void initMatrixPool() {
        mMatrixPool = new ObjectPool<Matrix>(OBJECT_POOL_SIZE) {

            @Override
            protected Matrix newInstance() {
                return new Matrix();
            }

            @Override
            protected Matrix resetInstance(Matrix matrix) {
                // 重置
                matrix.reset();
                return matrix;
            }
        };
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
    }

    /**
     * 初始化手势对象
     */
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(ZoomImageView.this.getContext(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDoubleTapEvent(MotionEvent e) {
                        // 触发双击事件
                        if (mCurrMode == ZOOM_MODE_SCROLL &&
                                !(mScaleAnimation != null && mScaleAnimation.isRunning())) {
                            doubleTap(e.getX(), e.getY());
                        }
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        //只有在单指模式结束之后才允许执行fling
                        if (mCurrMode == ZOOM_MODE_FREE &&
                                !(mScaleAnimation != null && mScaleAnimation.isRunning())) {
                            // 执行fling
                            fling(velocityX, velocityY);
                        }
                        return true;
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (mClickListener != null) {
                            mClickListener.onZoomClick(ZoomImageView.this);
                        }
                        return super.onSingleTapConfirmed(e);
                    }
                });
    }

    /**
     * 用于解决滑动冲突
     *
     * @param direction 方向
     * @return 是否可以横向滑动
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (mCurrMode == ZOOM_MODE_SCALE) {
            return true;
        }
        RectF bound = getImageBound(null);
        if (bound == null) {
            return false;
        }
        if (bound.isEmpty()) {
            return false;
        }
        if (direction > 0) {
            return bound.right > getWidth();
        } else {
            return bound.left < 0;
        }
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

    /**
     * 获取图片手势操作记录的外层矩阵
     */
    private Matrix getOuterMatrix(Matrix matrix) {
        if (matrix == null) {
            matrix = new Matrix(mOuterMatrix);
        } else {
            matrix.set(mOuterMatrix);
        }
        return matrix;
    }


    @Override
    public void onGlobalLayout() {
        // 对图片进行初始化处理
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
            if (width <= intrinsicWidth && height >= intrinsicHeight) {
                scale = width * 1.0f / intrinsicWidth;
            }
            if (width >= intrinsicWidth && height <= intrinsicHeight) {
                scale = height * 1.0f / intrinsicHeight;
            }
            if ((width <= intrinsicWidth && height <= intrinsicHeight)
                    || (width >= intrinsicWidth && height >= intrinsicHeight)) {
                scale = Math.min(width * 1.0f / intrinsicWidth, height * 1.0f / intrinsicHeight);
            }
            //将图片移动到控件的中心
            float dx = width / 2 - intrinsicWidth / 2;
            float dy = height / 2 - intrinsicHeight / 2;
            mInnerMatrix.postTranslate(dx, dy);
            mInnerMatrix.postScale(scale, scale, width / 2, height / 2);
            setImageMatrix(mInnerMatrix);
            isFirst = false;
        }
    }

    /**
     * 设置单击点击监听
     */
    public void addOnClickListener(OnZoomClickListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener must not be empty");
        }
        mClickListener = listener;
    }

    public interface OnZoomClickListener {
        void onZoomClick(View view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        //最后一个点抬起或者取消，结束所有模式
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            //如果之前是缩放模式,还需要触发一下缩放结束动画
            if (mCurrMode == ZOOM_MODE_SCROLL) {
                scaleEnd();
            }
            mCurrMode = ZOOM_MODE_FREE;
            Log.d("ACTION_UP: ", mCurrMode + "");
        }
        // 多指触发UP事件
        else if (action == MotionEvent.ACTION_POINTER_UP) {
            //多个手指情况下抬起一个手指,此时需要是缩放模式才触发
            if (event.getPointerCount() > 2) {
                // 如果还有两个以上的手指
                if (event.getPointerCount() > 2) {
                    //如果还没结束缩放模式，但是第一个点抬起了，那么让第二个点和第三个点作为缩放控制点
                    if (event.getAction() >> 8 == 0) {
                        saveScaleContext(event.getX(1),
                                event.getY(1),
                                event.getX(2),
                                event.getY(2));
                        //如果还没结束缩放模式，但是第二个点抬起了，那么让第一个点和第三个点作为缩放控制点
                    } else if (event.getAction() >> 8 == 1) {
                        saveScaleContext(event.getX(0),
                                event.getY(0),
                                event.getX(2),
                                event.getY(2));
                    }
                }
            }
            // 单指按下事件
            if (action == MotionEvent.ACTION_DOWN) {
                if (!(mScaleAnimation != null && mScaleAnimation.isRunning())) {
                    //停止所有动画
                    cancelAllAnimator();
                    //切换到缩放模式
                    mCurrMode = ZOOM_MODE_SCROLL;
                    mLastMovePoint.set(new PointF(event.getX(), event.getY()));
                }
                Log.d("ACTION_DOWN: ", mCurrMode + "");
            }
            // 多指触控按下事件
            else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                if (!(mScaleAnimation != null && mScaleAnimation.isRunning())) {
                    //停止所有动画
                    cancelAllAnimator();
                    //切换到缩放模式
                    mCurrMode = ZOOM_MODE_SCALE;
                    //保存缩放的两个手指
                    saveScaleContext(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                }
                Log.d("ACTION_POINTER_DOWN: ", mCurrMode + "");
            }
            //在矩阵动画过程中不允许启动滚动模式
            if (!(mScaleAnimation != null && mScaleAnimation.isRunning())) {
                //停止所有动画
                cancelAllAnimator();
                //切换到滚动模式
                mCurrMode = ZOOM_MODE_SCROLL;
                //保存触发点用于move计算差值
                mLastMovePoint.set(event.getX(), event.getY());
            }
        }
        // 手指移动事件
        else if (action == MotionEvent.ACTION_MOVE) {
            if (!(mScaleAnimation != null && mScaleAnimation.isRunning())) {
                //在滚动模式下移动
                if (mCurrMode == ZOOM_MODE_SCROLL) {
                    //每次移动产生一个差值累积到图片位置上
                    scrollBy(event.getX() - mLastMovePoint.x, event.getY() - mLastMovePoint.y);
                    //记录新的移动点
                    mLastMovePoint.set(event.getX(), event.getY());
                    //在缩放模式下移动
                } else if (mCurrMode == ZOOM_MODE_SCALE && event.getPointerCount() > 1) {
                    //两个缩放点间的距离
                    float distance = getDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    //保存缩放点中点
                    float[] lineCenter = getCenterPoint(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    mLastMovePoint.set(lineCenter[0], lineCenter[1]);
                    //处理缩放
                    scale(mScaleCenter, mScaleBase, distance, mLastMovePoint);
                }
            }
            Log.d("ACTION_MOVE: ", mCurrMode + "");
        }
        //无论如何都处理各种外部手势
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 记录缩放前的一些信息
     * <p>
     * 保存基础缩放值.
     * 保存图片缩放中点.
     *
     * @param x1 缩放第一个手指
     * @param y1 缩放第一个手指
     * @param x2 缩放第二个手指
     * @param y2 缩放第二个手指
     */
    private void saveScaleContext(float x1, float y1, float x2, float y2) {
        mScaleBase = getMatrixScale(mOuterMatrix) / getDistance(x1, y1, x2, y2);
        float[] center = inverseMatrixPoint(getCenterPoint(x1, y1, x2, y2), mOuterMatrix);
        mScaleCenter.set(center[0], center[1]);
        mLastMovePoint.set(center[0], center[1]);
    }

    /**
     * 获取两点的中点
     *
     * @param x1 点1
     * @param y1 点1
     * @param x2 点2
     * @param y2 点2
     * @return float[]{x, y}
     */
    private float[] getCenterPoint(float x1, float y1, float x2, float y2) {
        return new float[]{(x1 + x2) / 2f, (y1 + y2) / 2f};
    }

    /**
     * 计算点除以矩阵的值
     * <p>
     * matrix.mapPoints(unknownPoint) -> point
     * 已知point和matrix,求unknownPoint的值.
     *
     * @param point  对应的点
     * @param matrix 相应的矩阵
     * @return unknownPoint
     */
    private float[] inverseMatrixPoint(float[] point, Matrix matrix) {
        if (point != null && matrix != null) {
            float[] dst = new float[2];
            //计算matrix的逆矩阵
            Matrix inverse = mMatrixPool.obtain();
            matrix.invert(inverse);
            //用逆矩阵变换point到dst,dst就是结果
            inverse.mapPoints(dst, point);
            //清除临时变量
            mMatrixPool.revert(inverse);
            return dst;
        } else {
            return new float[2];
        }
    }

    /**
     * 缩放结束
     */
    private void scaleEnd() {
        if (!isReady()) {
            return;
        }
        //是否修正了位置
        boolean change = false;
        //获取图片整体的变换矩阵
        Matrix currentMatrix = mMatrixPool.obtain();
        getCurrentImageMatrix(currentMatrix);
        //整体缩放比例
        float currentScale = getMatrixScale(currentMatrix);
        //第二层缩放比例
        float outerScale = getMatrixScale(mOuterMatrix);
        //控件大小
        float displayWidth = getWidth();
        float displayHeight = getHeight();
        //最大缩放比例
        float maxScale = MAX_SCALE;
        //比例修正
        float scalePost = 1f;
        //位置修正
        float postX = 0;
        float postY = 0;
        //如果整体缩放比例大于最大比例，进行缩放修正
        if (currentScale > maxScale) {
            scalePost = maxScale / currentScale;
        }
        //如果缩放修正后整体导致第二层缩放小于1（就是图片比fit center状态还小），重新修正缩放
        if (outerScale * scalePost < 1f) {
            scalePost = 1f / outerScale;
        }
        //如果缩放修正不为1，说明进行了修正
        if (scalePost != 1f) {
            change = true;
        }
        //尝试根据缩放点进行缩放修正
        Matrix testMatrix = mMatrixPool.obtain();
        testMatrix.set(currentMatrix);
        testMatrix.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y);
        RectF testBound = mRectFPool.obtain();
        testBound.set(0, 0, getDrawable().getIntrinsicWidth(),
                getDrawable().getIntrinsicHeight());
        //获取缩放修正后的图片方框
        testMatrix.mapRect(testBound);
        //检测缩放修正后位置有无超出，如果超出进行位置修正
        if (testBound.right - testBound.left < displayWidth) {
            postX = displayWidth / 2f - (testBound.right + testBound.left) / 2f;
        } else if (testBound.left > 0) {
            postX = -testBound.left;
        } else if (testBound.right < displayWidth) {
            postX = displayWidth - testBound.right;
        }
        if (testBound.bottom - testBound.top < displayHeight) {
            postY = displayHeight / 2f - (testBound.bottom + testBound.top) / 2f;
        } else if (testBound.top > 0) {
            postY = -testBound.top;
        } else if (testBound.bottom < displayHeight) {
            postY = displayHeight - testBound.bottom;
        }
        //如果位置修正不为0，说明进行了修正
        if (postX != 0 || postY != 0) {
            change = true;
        }
        //只有有执行修正才执行动画
        if (change) {
            //计算结束矩阵
            Matrix animEnd = mMatrixPool.obtain();
            animEnd.set(mOuterMatrix);
            animEnd.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y);
            animEnd.postTranslate(postX, postY);
            //清理当前可能正在执行的动画
            cancelAllAnimator();
            //启动矩阵动画
            mScaleAnimation = new ScaleAnimation(mOuterMatrix, animEnd);
            mScaleAnimation.start();
            //清理临时变量
            mMatrixPool.revert(animEnd);
        }
        //清理临时变量
        mRectFPool.revert(testBound);
        mMatrixPool.revert(testMatrix);
        mMatrixPool.revert(currentMatrix);
    }

    /**
     * 执行惯性动画
     *
     * @param vx x方向速度
     * @param vy y方向速度
     */
    private void fling(float vx, float vy) {
        //清理当前可能正在执行的动画
        cancelAllAnimator();
        //创建惯性动画
        //FlingAnimator单位为 像素/帧,一秒60帧
        mFlingAnimation = new FlingAnimation(vx / 60f, vy / 60f);
        mFlingAnimation.start();
    }

    /**
     * 双击时触发
     *
     * @param x 双击事件的x坐标
     * @param y 双击事件的y坐标
     */
    private void doubleTap(float x, float y) {
        if (!isReady()) {
            return;
        }
        // 获取第一层变换矩阵
        Matrix innerMatrix = mMatrixPool.obtain();
        getInnerMatrix(innerMatrix);
        // 获取缩放比例
        float innerScale = getMatrixScale(innerMatrix);
        float outerScale = getMatrixScale(mOuterMatrix);
        float currentScale = innerScale * outerScale;
        //控件大小
        float displayWidth = getWidth();
        float displayHeight = getHeight();
        //最大放大大小
        float maxScale = MAX_SCALE / 2f;
        // 接下来要放大的大小
        float nextScale = calculateNextScale(innerScale, outerScale);
        //如果接下来放大大于最大值或者小于fit center值，则取边界
        if (nextScale > maxScale) {
            nextScale = maxScale;
        }
        if (nextScale < innerScale) {
            nextScale = innerScale;
        }
        // 动画所需的结果矩阵
        Matrix animEnd = mMatrixPool.obtain();
        getOuterMatrix(animEnd);
        animEnd.postScale(nextScale / currentScale, nextScale / currentScale, x, y);
        //将放大点移动到控件中心
        animEnd.postTranslate(displayWidth / 2f - x, displayHeight / 2f - y);
        //得到放大之后的图片方框
        Matrix testMatrix = mMatrixPool.obtain();
        testMatrix.set(innerMatrix);
        testMatrix.postConcat(animEnd);
        RectF testBound = mRectFPool.obtain();
        testBound.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        testMatrix.mapRect(testBound);
        //修正位置
        float postX = 0;
        float postY = 0;
        if (testBound.right - testBound.left < displayWidth) {
            postX = displayWidth / 2f - (testBound.right + testBound.left) / 2f;
        } else if (testBound.left > 0) {
            postX = -testBound.left;
        } else if (testBound.right < displayWidth) {
            postX = displayWidth - testBound.right;
        }
        if (testBound.bottom - testBound.top < displayHeight) {
            postY = displayHeight / 2f - (testBound.bottom + testBound.top) / 2f;
        } else if (testBound.top > 0) {
            postY = -testBound.top;
        } else if (testBound.bottom < displayHeight) {
            postY = displayHeight - testBound.bottom;
        }
        //应用修正位置
        animEnd.postTranslate(postX, postY);
        //清理当前可能正在执行的动画
        cancelAllAnimator();
        //启动矩阵动画
        mScaleAnimation = new ScaleAnimation(mOuterMatrix, animEnd);
        mScaleAnimation.start();
        //清理临时变量
        mRectFPool.revert(testBound);
        mMatrixPool.revert(testMatrix);
        mMatrixPool.revert(animEnd);
        mMatrixPool.revert(innerMatrix);
    }

    /**
     * 清除所有动画
     */
    private void cancelAllAnimator() {
        if (mScaleAnimation != null) {
            mScaleAnimation.cancel();
            mScaleAnimation = null;
        }
        if (mFlingAnimation != null) {
            mFlingAnimation.cancel();
            mFlingAnimation = null;
        }
    }

    /**
     * 计算接下来的图片缩放比例
     *
     * @param innerScale 当前内部矩阵的缩放值
     * @param outerScale 当前外部矩阵的缩放值
     * @return 接下来的缩放比例
     */
    private float calculateNextScale(float innerScale, float outerScale) {
        float currentScale = innerScale * outerScale;
        if (currentScale < MAX_SCALE / 2) {
            return MAX_SCALE / 2;
        } else {
            return innerScale;
        }

    }

    /**
     * 获取缩放大小
     *
     * @param matrix 对应的矩阵
     * @return 缩放大小
     */
    private float getMatrixScale(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }


    /**
     * 让图片移动一段距离
     * 若移动超过可移动范围，到范围边界位置
     *
     * @param xDiff x方向移动距离
     * @param yDiff y方向移动距离
     * @return 是否改了位置
     */
    private boolean scrollBy(float xDiff, float yDiff) {
        // 获取原图大小矩形
        RectF bound = mRectFPool.obtain();
        getImageBound(bound);
        // 获取控件大小
        float displayWidth = getWidth();
        float displayHeight = getHeight();
        //如果当前图片宽度小于控件宽度，则不能移动
        if (bound.right - bound.left < displayWidth) {
            xDiff = 0;
            //如果图片左边在移动后超出控件左边
        } else if (bound.left + xDiff > 0) {
            //如果在移动之前是没超出的，计算应该移动的距离
            if (bound.left < 0) {
                xDiff = -bound.left;
                //否则无法移动
            } else {
                xDiff = 0;
            }
            //如果图片右边在移动后超出控件右边
        } else if (bound.right + xDiff < displayWidth) {
            //如果在移动之前是没超出的，计算应该移动的距离
            if (bound.right > displayWidth) {
                xDiff = displayWidth - bound.right;
                //否则无法移动
            } else {
                xDiff = 0;
            }
        }
        //以下同理
        if (bound.bottom - bound.top < displayHeight) {
            yDiff = 0;
        } else if (bound.top + yDiff > 0) {
            if (bound.top < 0) {
                yDiff = -bound.top;
            } else {
                yDiff = 0;
            }
        } else if (bound.bottom + yDiff < displayHeight) {
            if (bound.bottom > displayHeight) {
                yDiff = displayHeight - bound.bottom;
            } else {
                yDiff = 0;
            }
        }
        mRectFPool.revert(bound);
        mOuterMatrix.postTranslate(xDiff, yDiff);
        invalidate();
        //检查是否有变化
        return xDiff != 0 || yDiff != 0;
    }

    /**
     * 获取图片边框矩形大小
     */
    private RectF getImageBound(RectF rectF) {
        if (rectF == null) {
            rectF = new RectF();
        } else {
            rectF.setEmpty();
        }
        // 申请一个空的matrix
        Matrix matrix = mMatrixPool.obtain();
        // 获取当前变换矩阵
        getCurrentImageMatrix(matrix);
        //对原图矩形进行变换得到当前显示矩形
        rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        matrix.mapRect(rectF);
        //释放临时matrix
        mMatrixPool.revert(matrix);
        return rectF;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isReady()) {
            Matrix matrix = mMatrixPool.obtain();
            // 在绘制前设置变换矩阵
            getCurrentImageMatrix(matrix);
            setImageMatrix(matrix);
            // 把矩阵归还到池中
            mMatrixPool.revert(matrix);
        }
        super.onDraw(canvas);
    }

    /**
     * 获取当前变化矩阵
     */
    private Matrix getCurrentImageMatrix(Matrix matrix) {
        //获取内部变换矩阵
        matrix = getInnerMatrix(matrix);
        //乘上外部变换矩阵
        matrix.postConcat(mOuterMatrix);
        return matrix;

    }

    /**
     * 获取内部变换矩阵
     *
     * @return
     */
    public Matrix getInnerMatrix(Matrix matrix) {
        if (matrix == null) {
            matrix = new Matrix();
        } else {
            matrix.reset();
        }
        if (isReady()) {
            //原图大小
            RectF tempSrc = mRectFPool.obtain();
            tempSrc.set(0, 0, getDrawable().getIntrinsicWidth(),
                    getDrawable().getIntrinsicHeight());
            //控件大小
            RectF tempDst = mRectFPool.obtain();
            tempDst.set(0, 0, getWidth(), getHeight());
            //计算fit center矩阵
            matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);
            //释放临时对象
            mRectFPool.revert(tempDst);
            mRectFPool.revert(tempSrc);
        }
        return matrix;
    }

    /**
     * 判断当前情况是否能执行手势相关计算
     */
    private boolean isReady() {
        return getDrawable() != null                        // 图片不为空
                && getDrawable().getIntrinsicWidth() > 0     // 图片可以获取宽度
                && getDrawable().getIntrinsicHeight() > 0    // 图片可以获取高度
                && getWidth() > 0                            // 控件有宽度
                && getHeight() > 0;                          // 控件有高度
    }

    /**
     * 计算两点之间的距离
     *
     * @param x1 第一个点的横坐标
     * @param y1 第一个点的从坐标
     * @param x2 第二个点的横坐标
     * @param y2 第二个点的纵坐标
     * @return 两个点之间的距离
     */
    private float getDistance(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 控制图片缩放的动画对象
     */
    private class ScaleAnimation extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        // 开始矩阵
        private float[] mStart = new float[9];
        // 结束矩阵
        private float[] mEnd = new float[9];
        // 中间结果矩阵
        private float[] mResult = new float[9];

        private ScaleAnimation(Matrix start, Matrix end) {
            super();
            // 构建缩放动画对象
            setFloatValues(0, 1.0F);
            setDuration(SCALE_ANIMATOR_DURATION);
            addUpdateListener(this);
            start.getValues(mStart);
            end.getValues(mEnd);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            // 获取动画进度
            float value = (Float) animation.getAnimatedValue();
            //根据动画进度计算矩阵中间插值
            for (int i = 0; i < 9; i++) {
                mResult[i] = mStart[i] + (mEnd[i] - mStart[i]) * value;
            }
            // 设置矩阵
            mOuterMatrix.setValues(mResult);
            // 重绘
            invalidate();
        }

    }

    /**
     * 实行对图像的缩放
     */
    private void scale(PointF scaleCenter, float scaleBase, float distance, PointF lineCenter) {
        //计算图片从fit center状态到目标状态的缩放比例
        float scale = scaleBase * distance;
        Matrix matrix = mMatrixPool.obtain();
        //按照图片缩放中心缩放，并且让缩放中心在缩放点中点上
        matrix.postScale(scale, scale, scaleCenter.x, scaleCenter.y);
        //让图片的缩放中点跟随手指缩放中点
        matrix.postTranslate(lineCenter.x - scaleCenter.x, lineCenter.y - scaleCenter.y);
        //应用变换
        mOuterMatrix.set(matrix);
        mMatrixPool.revert(matrix);
        //重绘
        invalidate();
    }

    /**
     * 惯性动画
     */
    private class FlingAnimation extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        // 速度向量
        private float mVectorX;
        private float mVectorY;

        /**
         * 创建惯性动画
         *
         * @param vectorX x方向惯性动画
         * @param vectorY y方向惯性动画
         */
        private FlingAnimation(float vectorX, float vectorY) {
            super();
            setFloatValues(0, 1f);
            setDuration(1000000);
            addUpdateListener(this);
            mVectorX = vectorX;
            mVectorY = vectorY;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            // 移动图像并给出结果
            boolean result = scrollBy(mVectorX, mVectorY);
            //衰减速度
            mVectorX *= FLING_DAMPING_FACTOR;
            mVectorY *= FLING_DAMPING_FACTOR;
            //速度太小或者不能移动了就结束
            if (!result || getDistance(0, 0, mVectorX, mVectorY) < 1f) {
                animation.cancel();
            }
        }

    }

}
