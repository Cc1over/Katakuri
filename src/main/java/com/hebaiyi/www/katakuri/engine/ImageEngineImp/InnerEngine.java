package com.hebaiyi.www.katakuri.engine.ImageEngineImp;

import android.widget.ImageView;

import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.imageLoader.Caramel;

public class InnerEngine implements ImageEngine {

    private static class Singleton {
        private static InnerEngine instance = new InnerEngine();
    }

    public static InnerEngine getInstance() {
        return Singleton.instance;
    }


    @Override
    public void loadThumbnailResize(int resize, int placeholderRes, String path, ImageView imageView) {
        Caramel.get()
                .load(path)
                .placeholder(placeholderRes)
                .resize(resize, resize)
                .into(imageView);
    }

    @Override
    public void loadThumbnail(int placeholderRes, String path, ImageView imageView) {
        Caramel.get()
                .load(path)
                .placeholder(placeholderRes)
                .into(imageView);
    }

    @Override
    public void loadThumbnailNoPlaceholder(String path, ImageView imageView) {
        Caramel.get()
                .load(path)
                .into(imageView);
    }

    @Override
    public void loadThumbnailFilter(int placeholderRes, String path, ImageView imageView, Caramel.Filter filter) {
        Caramel.get()
                .load(path)
                .filter(filter)
                .placeholder(placeholderRes)
                .into(imageView);
    }

}
