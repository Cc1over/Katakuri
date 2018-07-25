package com.hebaiyi.www.katakuri;

import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.engine.InnerImageEngine.InnerEngine;

public final class Config {

    private static final int MAX_SELECTABLE = 9;
    private static final float THUMBNAIL_SCALE = 0.5f;

    public int maxSelectable;
    public ImageEngine mImageEngine;
    public float thumbnailScale;
    public Katakuri.ImageType imageType;

    private static class Singleton {
        private static final Config instance = new Config();
    }

    /**
     * 获取配置参数的单例对象
     *
     * @return 配置参数对象
     */
    public static Config getInstance() {
        return Singleton.instance;
    }

    /**
     * 获取设置为默认值的配置参数对象
     *
     * @return 配置参数对象
     */
    public static Config getDefaultInstance() {
        Config config = getInstance();
        config.reDefault();
        return config;
    }

    /**
     * 把配置参数回调成默认值
     */
    private void reDefault() {
        maxSelectable = MAX_SELECTABLE;
        thumbnailScale = THUMBNAIL_SCALE;
        imageType = Katakuri.ImageType.ALL;
        mImageEngine = InnerEngine.getInstance();
    }

    private Config() {
        // 设置默认值
        reDefault();
    }

}
