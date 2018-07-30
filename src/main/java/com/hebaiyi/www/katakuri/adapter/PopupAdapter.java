package com.hebaiyi.www.katakuri.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.bean.Folder;
import com.hebaiyi.www.katakuri.engine.ImageEngine;

import java.util.List;

public class PopupAdapter extends BaseAdapter<Folder> {

    private ImageEngine mEngine;
    public static int TYPE_HEADER = 11;

    public PopupAdapter(List<Folder> list) {
        super(list, R.layout.popup_list_item);
        mEngine = Config.getDefaultInstance().getImageEngine();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void renewListItem(CommonViewHolder viewHolder, Folder folder, int position) {
        // 初始化控件
        ImageView ivFirst = viewHolder.getView(R.id.katakuri_popup_iv_first_picture);
        // 获取地址
        String path = folder.getFirstImagePath();
        // 设置标签
        ivFirst.setTag(path);
        // 加载图片
        mEngine.loadThumbnailNoPlaceholder(folder.getFirstImagePath(), ivFirst);
        TextView tvName = viewHolder.getView(R.id.katakuri_popup_tv_name);
        TextView tvNumber = viewHolder.getView(R.id.katakuri_popup_tv_number);
        // 设置内容
        tvName.setText(folder.getFolderName());
        tvNumber.setText(folder.getImageNum() + "");
    }

}
