package com.hebaiyi.www.katakuri.imageLoader;

import android.widget.ImageView;

public class Caramel {

    private static volatile Caramel singleton;
    private Dispatcher mDispatcher;

    private Caramel() {
        mDispatcher = Dispatcher.getInstance();
    }

    public static Caramel get() {
        if (singleton == null) {
            synchronized (Caramel.class) {
                if (singleton == null) {
                    // 创建对象
                    singleton = new Caramel();
                }
            }
        }
        return singleton;
    }

    public ActionCreator load(String uri) {
        return null;
    }

    public void into(ImageView imageView) {

    }


    public Caramel displayImage(String uri, ImageView imageView) {
        mDispatcher.performLoad(uri, imageView);
        return this;
    }

}
