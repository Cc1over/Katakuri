package com.hebaiyi.www.katakuri.imageLoader;

public class Caramel {

    private static volatile Caramel singleton;

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

    /**
     *  创建任务创建者
     * @param uri 对应的地址
     * @return 任务创建者对象
     */
    public ActionCreator load(String uri) {
        return new ActionCreator(uri);
    }


}
