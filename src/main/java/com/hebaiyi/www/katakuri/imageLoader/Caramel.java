package com.hebaiyi.www.katakuri.imageLoader;

import android.content.Context;
import android.widget.ImageView;

public class Caramel {

    private static volatile Caramel singleton;
    private Dispatcher mDispatcher;

    private Caramel() {
        mDispatcher = Dispatcher.getInstance();
    }

    public static Caramel with(Context context) {
        if (singleton == null) {
            synchronized (Caramel.class) {
                if (singleton == null) {
                    // 创建对象

                }
            }
        }
        return singleton;
    }


    public Caramel load(String uri) {
        return this;
    }

    public Caramel choose() {
        return this;
    }

    public Caramel into(ImageView imageView) {
        mDispatcher.performLoad(imageView);
        return this;
    }

}
