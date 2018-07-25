package com.hebaiyi.www.katakuri.util;

import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class ImageAware {

    private ImageView mImageView;
    private DisplayMetrics mMetrics;
    private ViewGroup.LayoutParams mLayoutParams;

    public ImageAware(ImageView imageView){
        mMetrics = imageView.getContext().getResources().getDisplayMetrics();
        mLayoutParams = imageView.getLayoutParams();
        mImageView = imageView;
    }

    public int getWidth(){
        int width = mImageView.getWidth();
        if (width <= 0) {
            width = mLayoutParams.width;
        }
        if (width <= 0) {
            width = mImageView.getMaxWidth();
        }
        if (width <= 0) {
            width = mMetrics.widthPixels;
        }
       return width;
    }

    public int getHeight(){
        int height = mImageView.getHeight();
        if (height <= 0) {
            height = mLayoutParams.height;
        }
        if (height <= 0) {
            height = mImageView.getMaxHeight();
        }
        if (height <= 0) {
            height = mMetrics.heightPixels;
        }
        return height;
    }

    public ImageView getImageView(){
        WeakReference<ImageView> imageAware = new WeakReference<>(mImageView);
        return imageAware.get();
    }


}
