package com.hebaiyi.www.katakuri.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.SparseBooleanArray;
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
    private Context mContext;

    public PerViewBottomAdapter(List<String> list, Context context) {
        super(list, R.layout.per_view_list_item);
        mContext = context;
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
        iv.setTag(s);
        if (mFlags.get(position)) {
            mEngine.loadThumbnailNoPlaceholder(s, iv);
        } else {
            mEngine.loadThumbnailOnlyFilter(s, iv, mFilter);
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
