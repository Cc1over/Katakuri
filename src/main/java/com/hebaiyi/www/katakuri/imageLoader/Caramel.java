package com.hebaiyi.www.katakuri.imageLoader;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

public class Caramel {

    private Dispatcher mDispatcher;
    static final int FINISH_IN_CACHE = 23;
    static final int FINISH_IN_LOADING = 24;

    public static Caramel get() {
        return Singleton.instance;
    }

    private static class Singleton {
        private static Caramel instance = new Caramel();
    }

    public Caramel() {
        UIHandler handler = new UIHandler(Looper.getMainLooper());
        mDispatcher = new Dispatcher(handler, Dispatcher.Type.LIFO);
    }

    /**
     *  bitmap处理
     * @param imageView imageView对象
     * @param bm bitmap对象
     * @param filter 过滤器
     */
    static void setBitmap(ImageView imageView, Bitmap bm, Caramel.Filter filter){
        if (filter == null) {
            imageView.setImageBitmap(bm);
        }else{
            // 对bitmap处理
            Bitmap bitmap = filter.bitmapFilter(bm);
            if (bitmap == null) {
                imageView.setImageBitmap(bm);
            }else{
                imageView.setImageBitmap(bitmap);
            }
            // 对imageView处理
            filter.imageFilter(imageView);
        }
    }

    /**
     * 创建任务创建者
     *
     * @param path 对应的地址
     * @return 任务创建者对象
     */
    public ActionCreator load(String path) {
        return new ActionCreator(path, mDispatcher);
    }

    private static class UIHandler extends android.os.Handler {

        UIHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 获取设置图片所需的参数
            switch (msg.what){
                case FINISH_IN_CACHE:
                    // 从内存获取bitmap
                    break;
                case FINISH_IN_LOADING:
                    // 本地加载获取bitmap
                    finishInLoading(msg);
                    break;
            }
        }

        /**
         *  加载获取bitmap
         */
        private void finishInLoading(Message msg){
            // 获取图片加载所需的参数
            ImageAction action = (ImageAction) msg.obj;
            Bitmap bm = action.getBitmap();
            String uri = action.getPath();
            ImageView imageView = action.getImageView();
            Caramel.Filter filter= action.getFilter();
            // 设置图片
            if (imageView.getTag().equals(uri)) {
                // 设置bitmap
                setBitmap(imageView,bm,filter);
                // 设置属性动画
                ObjectAnimator.ofFloat(imageView, "alpha", 0.5F, 1F)
                        .setDuration(250)
                        .start();
            }
        }

    }

    /**
     * 过滤器接口
     */
    public interface Filter {

        /**
         *  处理bitmap
         * @param bitmap 原来的bitmap对象
         * @return 处理后的bitmap对象
         */
        Bitmap bitmapFilter(Bitmap bitmap);

        /**
         *  处理imageView
         * @param imageView 对应的imageView对象
         */
        void imageFilter(ImageView imageView);
    }


}
