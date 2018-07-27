package com.hebaiyi.www.katakuri;

import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.engine.ImageEngineImp.InnerEngine;

public final class Config {

    private static final int MAX_SELECTABLE = 9;
    private static final float THUMBNAIL_SCALE = 0.5f;

    int maxSelectable;
    ImageEngine mImageEngine;
    float thumbnailScale;
    Katakuri.ImageType imageType;

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

    /**
     *  获取checkbox最大选择数量
     * @return checkbox最大选择数量
     */
    public int getMaxSelectable(){
        return maxSelectable;
    }

    /**
     *  获取外部设定的图片加载引擎
     * @return 图片加载引擎
     */
    public ImageEngine getImageEngine(){
        return mImageEngine;
    }

    /**
     *  获取缩略图的缩放大小
     * @return 缩放大小
     */
    public float getThumbnailScale() {
        return thumbnailScale;
    }

    private Config() {
        // 设置默认值
        reDefault();
    }

}
