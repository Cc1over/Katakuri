package com.hebaiyi.www.katakuri.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.adapter.ImageAdapter;
import com.hebaiyi.www.katakuri.bean.Folder;
import com.hebaiyi.www.katakuri.model.KatakuriModel;
import com.hebaiyi.www.katakuri.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class KatakuriActivity extends BaseActivity {

    private static final int FINISH_LOADING = 0;

    private RecyclerView mRcvContent;
    private Toolbar mTbTop;
    private KatakuriModel mModel;
    private SelectReceiver mReceiver;
    private Button mBtnSure;
    private ImageAdapter mAdapter;
    private KatakuriHandler mHandler;
    private List<Folder> mFolders;
    private List<String> mPaths;
    private ProgressDialog mProgressDialog;
    private Button mBtnPerView;
    private Button mBtnFolder;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_katakuri);
        // 初始化控件
        mRcvContent = findViewById(R.id.katakuri_rcv_content);
        mTbTop = findViewById(R.id.katakuri_tb_top);
        mModel = new KatakuriModel();
        mBtnSure = findViewById(R.id.katakuri_btn_sure);
        mBtnPerView = findViewById(R.id.katakuri_btn_per_view);
        mBtnFolder = findViewById(R.id.katakuri_btn_folder);
        // 设置状态栏颜色
        setStatusBarColor();
        // 初始化Toolbar
        initToolbar();
        // 设置按钮监听
        initClick();
        // 请求权限
        requestPermission();
        // 请求对话框
        initProgressDialog();
    }

    @Override
    protected void initVariables() {
        // 初始化Handler
        mHandler = new KatakuriHandler(this);
        // 注册广播
        registerReceiver();
    }

    @Override
    protected void loadData() {
        // 扫描图片
        scanPicture();
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        mTbTop.setTitle("");
        setSupportActionBar(mTbTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }


    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.hebaiyi.www.katakuri.KatakuriActivity.freshButton");
        mReceiver = new SelectReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(mReceiver);
    }

    /**
     * 初始化按钮监听
     */
    private void initClick() {
        mBtnFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBtnFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBtnPerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    /**
     * 初始化列表
     */
    private void initList() {
        mAdapter = new ImageAdapter(this, mPaths);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRcvContent.setLayoutManager(manager);
        mRcvContent.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
     * 扫描图片
     */
    private void scanPicture() {
        mModel.scan(this, new KatakuriModel.KatakuriModelCallback() {
            @Override
            public void onSuccess(List<Folder> folders, List<String> paths) {
                // 保存数据
                mFolders = folders;
                mPaths = paths;
                // 更新UI
                mHandler.sendEmptyMessage(FINISH_LOADING);
            }

            @Override
            public void onFail() {

            }
        });
    }

    /**
     * 请求访问权限
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            // 搜寻图片
            scanPicture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanPicture();
                } else {
                    Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.pink));
    }

    /**
     * 初始化加载对话框
     */
    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }


    private class SelectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int currNum = intent.getIntExtra("curr_num", 0);
            String sure = getString(R.string.katakuri_btn_sure);
            String perView = getString(R.string.katakuri_btn_per_view);
            if (currNum == 0) {
                mBtnSure.setText(sure);
                mBtnPerView.setText(perView);
            } else {
                String numSure = StringUtil.buildString(sure, "(", currNum + "", ")");
                String numPerView = StringUtil.buildString(perView, "(", currNum + "", ")");
                mBtnSure.setText(numSure);
                mBtnPerView.setText(numPerView);
            }
        }
    }

    private static class KatakuriHandler extends Handler {

        private final WeakReference<KatakuriActivity> mActivity;

        private KatakuriHandler(KatakuriActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            KatakuriActivity katy = mActivity.get();
            if (katy == null) {
                return;
            }
            switch (msg.what) {
                case FINISH_LOADING:
                    // 初始化列表
                    katy.initList();
                    // 设置按钮内容
                    katy.mBtnFolder.setText("所有图片");
                    // 关闭加载框
                    katy.mProgressDialog.dismiss();
                    break;
            }
        }
    }

}
