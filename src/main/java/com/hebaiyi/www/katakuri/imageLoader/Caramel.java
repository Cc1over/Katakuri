package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

public class Caramel {

    private Dispatcher mDispatcher;

    public static Caramel get() {
        return Singleton.instance;
    }

    private static class Singleton {
        private static Caramel instance = new Caramel();
    }

    private Caramel() {
        UIHandler handler = new UIHandler(Looper.getMainLooper());
        mDispatcher = new Dispatcher(handler,Dispatcher.Type.LIFO);
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

        UIHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 获取设置图片所需的参数
            ImageAction action = (ImageAction) msg.obj;
            Bitmap bm = action.getBitmap();
            String uri = action.getPath();
            ImageView imageView = action.getImageView();
            // 设置图片
            if (imageView.getTag().equals(uri)) {
                imageView.setImageBitmap(bm);
            }
        }
    }


}
