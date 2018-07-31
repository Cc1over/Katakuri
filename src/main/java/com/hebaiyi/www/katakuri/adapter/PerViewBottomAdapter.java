package com.hebaiyi.www.katakuri.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.imageLoader.Caramel;

import java.util.List;

public class PerViewBottomAdapter extends BaseAdapter<String> {

    private ImageEngine mEngine;
    private Caramel.Filter mFilter;
    private SparseBooleanArray mFlags;
    private SparseArray<View> mCheeks;

    public PerViewBottomAdapter(List<String> list) {
        super(list, R.layout.per_view_list_item);
        mEngine = Config.getInstance().getImageEngine();
        // 初始化过滤器
        mFilter = new Caramel.Filter() {
            @Override
            public Bitmap bitmapFilter(Bitmap bitmap) {
                return bitmap;
            }

            @Override
            public void imageFilter(ImageView imageView) {
                darkImageView(imageView);
            }
        };
        // 初始化标记容器
        mFlags = new SparseBooleanArray();
        for (int i = 0; i < list.size(); i++) {
            mFlags.put(i, true);
        }
        // 初始化边框view
        mCheeks = new SparseArray<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void renewListItem(CommonViewHolder viewHolder, String s, int position) {
        ImageView iv = viewHolder.getView(R.id.per_view_iv_rotation);
        iv.setTag(s);
        // 根据选择情况添加滤镜
        if (mFlags.get(position)) {
            mEngine.loadThumbnailNoPlaceholder(s, iv);
        } else {
            mEngine.loadThumbnailOnlyFilter(s, iv, mFilter);
        }
        // 初始化保存用于显示边框的覆盖view
        if (mCheeks.size() != getData().size()) {
            View v = viewHolder.getView(R.id.per_view_v_cheek);
            mCheeks.put(position, v);
            // 默认第一项出现边框
            if (position == 0) {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 局部添加滤镜
     *
     * @param position 子项索引
     */
    public void setFilterOnItem(int position) {
        mFlags.put(position, false);
        this.notifyItemChanged(position);
    }

    /**
     * 局部刷新清除滤镜
     *
     * @param position 子项索引
     */
    public void clearFilterOnItem(int position) {
        mFlags.put(position, true);
        this.notifyItemChanged(position);
    }

    /**
     * 给imageView设置边框
     */
    public void setCheek(int position) {
        for (int i = 0; i < mCheeks.size(); i++) {
            if (i == position) {
                mCheeks.get(i).setVisibility(View.VISIBLE);
            } else {
                mCheeks.get(i).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 让相应的ImageView变暗淡
     */
    private void darkImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            drawable = imageView.getBackground();
        }
        if (drawable != null) {
            // 添加滤镜
            drawable.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
        }
    }

}