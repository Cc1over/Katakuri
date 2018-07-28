package com.hebaiyi.www.katakuri.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.adapter.ImageAdapter;
import com.hebaiyi.www.katakuri.model.KatakuriModel;

import java.util.ArrayList;
import java.util.List;

public class KatakuriActivity extends BaseActivity {

    private RecyclerView mRcvContent;
    private List<String> childPath = new ArrayList<>();
    private Toolbar mTbTop;
    private KatakuriModel mModel;
    private SelectReceiver mReceiver;
    private Button mBtnSure;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_katakuri);
        mRcvContent = findViewById(R.id.katakuri_rcv_content);
        mTbTop = findViewById(R.id.katakuri_tb_top);
        mModel = new KatakuriModel();
        mBtnSure = findViewById(R.id.katakuri_btn_sure);
        // 设置状态栏颜色
        setStatusBarColor();
        // 初始化Toolbar
        initToolbar();
        // 请求权限
        requestPermission();
        // 初始化列表
        initList();
    }

    @Override
    protected void initVariables() {
       // 注册广播
        registerReceiver();
    }

    @Override
    protected void loadData() {

    }

    private void initToolbar() {
        mTbTop.setTitle("");
        setSupportActionBar(mTbTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }


    /**
     *  注册广播
     */
    private void registerReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.hebaiyi.www.katakuri.KatakuriActivity.freshButton");
        mReceiver = new SelectReceiver();
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(mReceiver);
    }

    /**
     * 初始化列表
     */
    private void initList() {
        ImageAdapter adapter = new ImageAdapter(this,childPath);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRcvContent.setLayoutManager(manager);
        mRcvContent.setAdapter(adapter);
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
        childPath = mModel.scanPicture(this);
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
    private void setStatusBarColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.pink));
    }

    private class SelectReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
             String text = intent.getStringExtra("update_content");
             mBtnSure.setText(text);
        }
    }

}
