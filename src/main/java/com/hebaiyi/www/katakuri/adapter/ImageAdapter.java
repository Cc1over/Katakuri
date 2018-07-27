package com.hebaiyi.www.katakuri.adapter;

import android.util.SparseBooleanArray;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.util.ViewUtil;

import java.util.List;

public class ImageAdapter extends BaseAdapter<String> {

    private ImageEngine mEngine;
    private int mMaxSelection;
    private SparseBooleanArray mFlags;

    public ImageAdapter(List<String> list) {
        super(list, R.layout.katakuri_list_item);
        // 获取图片加载引擎
        mEngine = Config.getInstance().getImageEngine();
        // 获取所需数据
        mMaxSelection = Config.getInstance().getMaxSelectable();
        // 初始化状态标志容器
        mFlags = new SparseBooleanArray();
        // 初始化状态容器
        initSparseBooleanArray();
    }

    @Override
    public void renewListItem(CommonViewHolder viewHolder, String path, int position) {
        // 获取view
        ImageView iv = viewHolder.getView(R.id.list_item_iv_picture);
        CheckBox cb = viewHolder.getView(R.id.list_item_cb_select);
        // 加载图片
        mEngine.loadThumbnailResize(100, R.drawable.list_item_iv_default, path, iv);
        // 对CheckBox进行处处理,防止错乱
        treatCheckBox(cb, position,iv);
    }

    /**
     *  初始化状态容器
     */
    private void initSparseBooleanArray(){
        // 获取数据
        List<String> data = getData();
        if(data==null){
            throw new NullPointerException("data is not exist");
        }
        // 初始化
        for(int i=0;i<data.size();i++){
            mFlags.append(i,false);
        }
    }

    /**
     * 处理对应的CheckBox，防止复用造成的错乱
     *
     * @param checkBox 相应的checkBox
     */
    private void treatCheckBox(CheckBox checkBox, final int position, final ImageView imageView) {
        // 扩大点击范围
        ViewUtil.expandViewTouchDelegate(checkBox, 60, 60, 60, 60);
        // 设置标记
        checkBox.setTag(position);
        // 设置监听
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mFlags.put(position,b);
                if(b){

                }else {

                }
            }
        });
        checkBox.setChecked(mFlags.get(position));

    }




}
