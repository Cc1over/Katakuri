package com.hebaiyi.www.katakuri.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.adapter.BaseAdapter;
import com.hebaiyi.www.katakuri.adapter.PopupAdapter;
import com.hebaiyi.www.katakuri.bean.Folder;

import java.util.List;

public class FolderPopupWindow extends PopupWindow {


    @RequiresApi(api = Build.VERSION_CODES.M)
    public FolderPopupWindow(Context context, View rootView,
                             List<Folder> folders, final FolderWindowCallback callback) {
        super();
        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_katakuri, null);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(rootView.getMeasuredHeight() / 12 * 9);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(context.getColor(R.color.white)));
        setAnimationStyle(R.style.anim_katakuri_popup);
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callback.windowDissmiss();
            }
        });
        RecyclerView rcv = contentView.findViewById(R.id.popup_rcv_list);
        // 设置分界线
        rcv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        // 设置recyclerView
        final PopupAdapter adapter = new PopupAdapter(folders);
        rcv.setLayoutManager(new LinearLayoutManager(context));
        rcv.setAdapter(adapter);
        // 设置监听
        adapter.setItemClickListener(new BaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (adapter.getItemViewType(position) == PopupAdapter.TYPE_HEADER) {
                    callback.firstItemClick();
                } else {
                    callback.commonItemClick(position);
                }
            }
        });
    }

    public interface FolderWindowCallback {
        void firstItemClick();

        void commonItemClick(int position);

        void windowDissmiss();
    }

}
