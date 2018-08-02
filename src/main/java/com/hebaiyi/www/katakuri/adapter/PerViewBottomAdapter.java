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
    private int mCurrPosition;
    private boolean needToFilter;

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
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void renewListItem(CommonViewHolder viewHolder, String s, int position) {
        ImageView iv = viewHolder.getView(R.id.per_view_iv_rotation);
        View v = viewHolder.getView(R.id.per_view_v_cheek);
        iv.setTag(s);
        // 根据选择情况添加滤镜
        if (mFlags.get(position) || !needToFilter) {
            mEngine.loadThumbnail(R.drawable.list_item_iv_default, s, iv);
        } else {
            mEngine.loadThumbnailOnlyFilter(s, iv, mFilter);
        }
        // 根据情况设置边框
        if (mCurrPosition == position) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
    }

    /**
     * 局部添加滤镜
     *
     * @param position 子项索引
     */
    public void setFilterOnItem(int position) {
        needToFilter = true;
        mFlags.put(position, false);
        this.notifyItemChanged(position);
    }

    /**
     * 局部刷新清除滤镜
     *
     * @param position 子项索引
     */
    public void clearFilterOnItem(int position) {
        needToFilter = false;
        mFlags.put(position, true);
        this.notifyItemChanged(position);
    }

    public void deleteItem(int position) {
        getData().remove(position);
        mCurrPosition = -1;
        this.notifyDataSetChanged();
    }

    public void addItem(String path) {
        getData().add(path);
        mFlags.put(getData().size() - 1, true);
        this.notifyDataSetChanged();
    }

    /**
     * 给imageView设置边框
     */
    public void setCheek(int position) {
        if(!mFlags.get(position)){
            needToFilter = true;
        }
        mCurrPosition = position;
        notifyDataSetChanged();
    }

    /**
     * 给imageView设置边框
     */
    public void hideCheek() {
        mCurrPosition = -1;
        notifyDataSetChanged();
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
