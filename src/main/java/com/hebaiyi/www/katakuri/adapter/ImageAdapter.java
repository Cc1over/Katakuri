package com.hebaiyi.www.katakuri.adapter;

import android.widget.ImageView;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.engine.ImageEngine;

import java.util.List;

public class ImageAdapter extends BaseAdapter<String> {

    private ImageEngine mEngine;

    public ImageAdapter(List<String> list) {
        super(list, R.layout.katakuri_list_item);
        // 获取图片加载引擎
        mEngine = Config.getInstance().getImageEngin();
    }

    @Override
    public void renewListItem(CommonViewHolder viewHolder, String path) {
        ImageView iv = viewHolder.getView(R.id.list_item_iv_picture);
        // 加载图片
        mEngine.loadThumbnailResize(100, R.drawable.list_item_iv_default, path, iv);
    }
}
