package com.hebaiyi.www.katakuri.adapter;

import android.util.Log;
import android.widget.ImageView;

import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.imageLoader.Caramel;

import java.util.List;

public class ImageAdapter extends BaseAdapter<String> {

    public ImageAdapter(List<String> list) {
        super(list, R.layout.katakuri_list_item);
    }

    @Override
    public void renewListItem(CommonViewHolder viewHolder, String path) {
        ImageView iv = viewHolder.getView(R.id.list_item_iv_picture);
        iv.setImageResource(R.drawable.list_item_iv_default);
        Caramel.get().load(path).into(iv);

    }
}
