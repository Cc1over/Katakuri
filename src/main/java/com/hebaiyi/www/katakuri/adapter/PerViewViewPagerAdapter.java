package com.hebaiyi.www.katakuri.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PerViewViewPagerAdapter extends PagerAdapter {

    private List<View> mViews;
    private ViewPagerClickCallBack mCallBack;

    public PerViewViewPagerAdapter(List<View> views) {
        mViews = views;
    }


    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View currView = mViews.get(position);
        currView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onViewPagerItemClick();
            }
        });
        container.addView(currView);
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * 设置ViewPager点击回调
     *
     * @param callBack 回调对象
     */
    public void setViewPagerClick(ViewPagerClickCallBack callBack) {
        this.mCallBack = callBack;
    }

    public interface ViewPagerClickCallBack {
        void onViewPagerItemClick();
    }


}
