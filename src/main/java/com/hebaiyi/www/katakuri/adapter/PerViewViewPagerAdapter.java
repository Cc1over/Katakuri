package com.hebaiyi.www.katakuri.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.ui.ScaleImageView;
import com.hebaiyi.www.katakuri.ui.ZoomImageView;

import java.util.List;

public class PerViewViewPagerAdapter extends PagerAdapter
        implements View.OnClickListener, ZoomImageView.OnZoomClickListener{

    private List<String> mSelections;
    private ViewPagerClickCallBack mCallBack;
    private ImageEngine mEngine;

    public PerViewViewPagerAdapter(List<String> selections) {
        mSelections = selections;
        mEngine = Config.getInstance().getImageEngine();
    }


    @Override
    public int getCount() {
        return mSelections.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.per_view_pager, null);
        ZoomImageView iv = view.findViewById(R.id.per_view_iv_content);
        iv.addOnClickListener(this);
        view.setTag(position);
        mEngine.loadThumbnailResize(1080, mSelections.get(position), iv);
        container.addView(view);
        return view;
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

    @Override
    public void onClick(View v) {
        mCallBack.onViewPagerItemClick();
    }


    @Override
    public void onZoomClick(View view) {
        mCallBack.onViewPagerItemClick();
    }

    public interface ViewPagerClickCallBack {
        void onViewPagerItemClick();
    }

}
