package com.hebaiyi.www.katakuri.imageLoader;

import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageUtil {

    public static int getWidth(ImageView imageView) {
        DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        int width = imageView.getWidth();
        if (width <= 0) {
            width = params.width;
        }
        if (width <= 0) {
            width = imageView.getMaxWidth();
        }
        if (width <= 0) {
            width = metrics.widthPixels;
        }
        return width;
    }

    public static int getHeight(ImageView imageView) {
        DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        int height = imageView.getHeight();
        if (height <= 0) {
            height = params.height;
        }
        if (height <= 0) {
            height = imageView.getMaxHeight();
        }
        if (height <= 0) {
            height = metrics.heightPixels;
        }
        return height;
    }


}
