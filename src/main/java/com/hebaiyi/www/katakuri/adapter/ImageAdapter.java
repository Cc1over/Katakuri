package com.hebaiyi.www.katakuri.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.imageLoader.Caramel;
import com.hebaiyi.www.katakuri.util.ToastUtil;
import com.hebaiyi.www.katakuri.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageAdapter extends BaseAdapter<String> {

    private ImageEngine mEngine;
    private final int mMaxSelection;
    private int mNotSelection;
    public HashMap<String, Boolean> mFlags;
    private Context mContext;
    private Caramel.Filter mFilter;
    private List<String> mSelections;

    public ImageAdapter(Context context, List<String> list) {
        super(list, R.layout.katakuri_list_item);
        // 获取上下文
        mContext = context.getApplicationContext();
        // 获取图片加载引擎
        mEngine = Config.getInstance().getImageEngine();
        // 获取所需数据
        mMaxSelection = Config.getInstance().getMaxSelectable();
        // 初始化状态标志容器
        mFlags = new HashMap<>();
        // 初始化状态容器
        initSparseBooleanArray();
        // 初始化过滤器
        initFilter();
        // 初始化选择容器
        mSelections = new ArrayList<>();
    }

    @Override
    public void renewListItem(CommonViewHolder viewHolder, String path, int position) {
        // 获取view
        ImageView iv = viewHolder.getView(R.id.list_item_iv_picture);
        CheckBox cb = viewHolder.getView(R.id.list_item_cb_select);
        // 加载图片
        if (mFlags.get(getData().get(position)) == null || mFlags.get(getData().get(position))) {
            // 加载图片添加过滤器
            mEngine.loadThumbnailFilter(R.drawable.list_item_iv_default, path, iv, mFilter);
        } else {
            // 加载图片不添加过滤器
            mEngine.loadThumbnail(R.drawable.list_item_iv_default, path, iv);
        }
        // 对CheckBox进行处处理,防止错乱
        treatCheckBox(cb, position, iv);
    }


    /**
     * 初始化状态容器
     */
    private void initSparseBooleanArray() {
        // 获取数据
        List<String> data = getData();
        if (data == null) {
            throw new NullPointerException("data is not exist");
        }
        // 初始化
        for (int i = 0; i < data.size(); i++) {
            mFlags.put(data.get(i), false);
        }
    }

    /**
     * 初始化过滤器
     */
    private void initFilter() {
        mFilter = new Caramel.Filter() {
            @Override
            public Bitmap bitmapFilter(Bitmap bitmap) {
                return bitmap;
            }

            @Override
            public void imageFilter(ImageView imageView) {
                // 让imageView变暗
                darkImageView(imageView);
            }
        };
    }

    /**
     * 处理对应的CheckBox，防止复用造成的错乱
     *
     * @param checkBox 相应的checkBox
     */
    private void treatCheckBox(final CheckBox checkBox, final int position, final ImageView imageView) {
        // 设置标签
        checkBox.setTag(position);
        // 扩大点击范围
        ViewUtil.expandViewTouchDelegate(checkBox, 20, 20, 20, 20);
        // 设置监听
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // checkbox点击
                    isCheck(position, checkBox, imageView);
                } else {
                    // checkbox点击取消
                    isNotCheck(position, imageView);
                }
            }
        });
        // 设置状态
        checkBox.setChecked(mFlags.get(getData().get(position)));
    }

    /**
     * CheckBox点击点击后触发的事件
     */
    private void isCheck(int position, CheckBox checkBox, ImageView imageView) {
        if (mNotSelection < mMaxSelection) {
            // 恢复状态时调用直接退出
            if (mFlags.get(getData().get(position))) {
                return;
            }
            // 保存状态
            mFlags.put(getData().get(position), true);
            // 增加当前选择数
            mNotSelection++;
            // 让ImageView变暗
            darkImageView(imageView);
            // 通知按钮改变现实内容
            postButtonChange();
            // 添加到选择容器
            mSelections.add(getData().get(position));
        } else {
            if (!positionInSelect(getData().get(position))) {
                // 保存状态
                mFlags.put(getFormatDate().get(position), false);
                checkBox.setChecked(false);
                ToastUtil.showToast(mContext, "最多只能选择" + mMaxSelection + "项", Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * checkBox取消点击后触发事件
     */
    private void isNotCheck(int position, ImageView imageView) {
        if (positionInSelect(getData().get(position))) {
            // 减少当前选择数
            mNotSelection--;
            // 保存状态
            mFlags.put(getFormatDate().get(position), false);
            // 让ImageView恢复
            lightImageView(imageView);
            // 通知按钮更改内容
            postButtonChange();
            // 从容器中移除
            mSelections.remove(getData().get(position));
        }
    }

    /**
     * 广播通知主界面按钮改变
     */
    private void postButtonChange() {
        Intent intent = new Intent();
        intent.putExtra("curr_num", mNotSelection);
        intent.setAction("com.hebaiyi.www.katakuri.KatakuriActivity.freshButton");
        mContext.sendBroadcast(intent);
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
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * 让相应的ImageView恢复
     */
    private void lightImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            drawable = imageView.getBackground();
        }
        if (drawable != null) {
            // 清除滤镜
            drawable.clearColorFilter();
        }
    }


    /**
     * 判断位置的状态
     */
    private boolean positionInSelect(String path) {
        for (int i = 0; i < mFlags.size(); i++) {
            if (mFlags.get(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通知适配器选中的项目发生刷新
     *
     * @param map 状态保存容器
     */
    public void notifySelectionChange(HashMap<String, Boolean> map) {
        String[] keySet = map.keySet().toArray(new String[0]);
        for (String path : keySet) {
            boolean isCheck = map.get(path);
            if (!isCheck) {
                if (positionInSelect(path)) {
                    // 当前选择数简易
                    mNotSelection--;
                    // 从选择容器中移除
                    mSelections.remove(path);
                    // 通知局部更新
                    this.notifyItemChanged(getData().indexOf(path));
                }
            } else {
                if (!positionInSelect(path)) {
                    // 当前选择数加一
                    mNotSelection++;
                    // 添加到已选容器中
                    mSelections.add(path);
                    // 通知局部更新
                    this.notifyItemChanged(getData().indexOf(path));
                }
            }
            // 保存标记
            mFlags.put(path, map.get(path));
        }
        // 通知按钮内容变化
        postButtonChange();
    }

    /**
     * 返回保存已经选择的选项的列表
     */
    public List<String> getSelectedItems() {
        return mSelections;
    }

}

