package com.hebaiyi.www.katakuri.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.imageLoader.Caramel;
import com.hebaiyi.www.katakuri.util.ViewUtil;

import java.util.List;

public class ImageAdapter extends BaseAdapter<String> {

    private ImageEngine mEngine;
    private final int mMaxSelection;
    private int mNotSelection;
    private SparseBooleanArray mFlags;
    private Context mContext;
    private Caramel.Filter mFilter;

    public ImageAdapter(Context context, List<String> list) {
        super(list, R.layout.katakuri_list_item);
        // 获取上下文
        mContext = context;
        // 获取图片加载引擎
        mEngine = Config.getInstance().getImageEngine();
        // 获取所需数据
        mMaxSelection = Config.getInstance().getMaxSelectable();
        // 初始化状态标志容器
        mFlags = new SparseBooleanArray();
        // 初始化状态容器
        initSparseBooleanArray();
        // 初始化过滤器
        initFilter();
    }

    @Override
    public void renewListItem(CommonViewHolder viewHolder, String path, int position) {
        // 获取view
        ImageView iv = viewHolder.getView(R.id.list_item_iv_picture);
        CheckBox cb = viewHolder.getView(R.id.list_item_cb_select);
        // 加载图片
        if(mFlags.get(position)){
            // 加载图片添加过滤器
            mEngine.loadThumbnailFilter(R.drawable.list_item_iv_default, path, iv, mFilter);
        }else{
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
            mFlags.append(i, false);
        }
    }

    /**
     *  初始化过滤器
     */
    private void initFilter(){
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
        ViewUtil.expandViewTouchDelegate(checkBox, 60, 60, 60, 60);
        // 设置监听
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // checkbox点击
                    isCheck(position, checkBox, imageView);
                } else {
                    // checkbox点击取消
                    noCheck(position, imageView);
                }
            }
        });
        // 设置状态
        checkBox.setChecked(mFlags.get(position));
    }

    /**
     * CheckBox点击点击后触发的事件
     */
    private void isCheck(int position, CheckBox checkBox, ImageView imageView) {
        if (mNotSelection < mMaxSelection) {
            // 恢复状态时调用直接退出
            if (mFlags.get(position)) {
                return;
            }
            // 保存状态
            mFlags.put(position, true);
            // 增加当前选择数
            mNotSelection++;
            // 让ImageView变暗
            darkImageView(imageView);
            // 通知按钮改变现实内容
            postButtonChange();
        } else {
            if (!positionInSelect(position)) {
                // 保存状态
                mFlags.put(position, false);
                checkBox.setChecked(false);
                Toast.makeText(mContext, "最多只能选择" + mMaxSelection + "项", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * checkBox取消点击后触发事件
     */
    private void noCheck(int position, ImageView imageView) {
        if (positionInSelect(position)) {
            // 减少当前选择数
            mNotSelection--;
            // 保存状态
            mFlags.put(position, false);
            // 让ImageView恢复
            lightImageView(imageView);
            // 通知按钮更改内容
            postButtonChange();
        }
    }

    /**
     * 广播通知主界面按钮改变
     */
    private void postButtonChange() {
        Intent intent = new Intent();
        String text = createButtonText();
        intent.putExtra("update_content", text);
        intent.setAction("com.hebaiyi.www.katakuri.KatakuriActivity.freshButton");
        mContext.sendBroadcast(intent);
    }

    /**
     * 构建按钮文字内容
     *
     * @return 文字内容
     */
    private String createButtonText() {
        StringBuilder builder = new StringBuilder();
        builder.append("确定").append("(").append(mNotSelection).append("/").append(mMaxSelection).append(")");
        if (mNotSelection != 0) {
            return builder.toString();
        } else {
            return "确定";
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
    private boolean positionInSelect(int position) {
        for (int i = 0; i < mFlags.size(); i++) {
            if (mFlags.get(i) && i == position) {
                return true;
            }
        }
        return false;
    }

}

