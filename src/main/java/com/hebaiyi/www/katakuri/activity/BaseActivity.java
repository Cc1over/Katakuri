package com.hebaiyi.www.katakuri.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化变量
        initVariables();
        // 初始化布局
        initView(savedInstanceState);
        // 调用API数据
        loadData();
    }

    protected abstract void initView(Bundle savedInstanceState);
    protected abstract void initVariables();
    protected abstract void loadData();

}
