package com.hebaiyi.www.katakuri.engine;

import android.widget.ImageView;

import com.hebaiyi.www.katakuri.imageLoader.Caramel;

public interface ImageEngine {

    void loadThumbnailResize(int resize, String path, ImageView imageView);

    void loadThumbnail(int placeholderRes,String path,ImageView imageView);

    void loadThumbnailNoPlaceholder(String path,ImageView imageView);

    void loadThumbnailFilter(int placeholderRes, String path, ImageView imageView, Caramel.Filter filter);

    void loadThumbnailOnlyFilter(String path, ImageView imageView, Caramel.Filter filter);

    void loadArtWorkFromLocal(String path,ImageView imageView);

}
