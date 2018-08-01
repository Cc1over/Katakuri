package com.hebaiyi.www.katakuri.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.ui.MatrixImageView;

import java.util.List;

public class PerViewViewPagerAdapter extends PagerAdapter {

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
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.per_view_pager, null);
        ImageView iv = view.findViewById(R.id.per_view_iv_content);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onViewPagerItemClick();
            }
        });
        mEngine.loadArtWorkFromLocal(mSelections.get(position),iv);
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

    public interface ViewPagerClickCallBack {
        void onViewPagerItemClick();
    }

}
