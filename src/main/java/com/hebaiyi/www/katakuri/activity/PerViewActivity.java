package com.hebaiyi.www.katakuri.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.adapter.BaseAdapter;
import com.hebaiyi.www.katakuri.adapter.PerViewBottomAdapter;
import com.hebaiyi.www.katakuri.adapter.PerViewViewPagerAdapter;
import com.hebaiyi.www.katakuri.engine.ImageEngine;
import com.hebaiyi.www.katakuri.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PerViewActivity extends BaseActivity {

    public static int REQUEST_CODE = 55;

    private Toolbar mTbTop;
    private List<String> mSelectionList;
    private ViewPager mVpContent;
    private RecyclerView mRcvRotation;
    private CheckBox mCbSelection;
    private Button mBtnSure;
    private TextView mTvNum;
    private int mCurrPosition;
    private PerViewBottomAdapter mAdapter;
    private HashMap<String, Boolean> mFlags;
    private int mCurrNum;

    public static void actionStart(Activity activity, ArrayList<String> paths) {
        Intent intent = new Intent(activity, PerViewActivity.class);
        intent.putStringArrayListExtra("select_paths", paths);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_per_view);
        // 初始化控件
        mTbTop = findViewById(R.id.per_view_tb_top);
        mVpContent = findViewById(R.id.per_view_vp_content);
        mRcvRotation = findViewById(R.id.per_view_rcv_rotation);
        mCbSelection = findViewById(R.id.per_view_cb_select);
        mBtnSure = findViewById(R.id.per_view_btn_sure);
        mTvNum = findViewById(R.id.per_view_tv_num);
        // 设置Toolbar
        setToolBar();
        // 设置按钮文字
        String numSure = StringUtil.buildString("确定", "(", mSelectionList.size() + "", ")");
        mBtnSure.setText(numSure);
        // 设置数字标题
        String num = StringUtil.buildString("1", "/", mSelectionList.size() + "");
        mTvNum.setText(num);
        // 初始化状态栏颜色
        setStatusBarColor(getResources().getColor(R.color.pink));
        // 初始化viewPager
        initViewPager();
        // 初始化RecyclerView
        initRecyclerView();
        // 设置ViewPager滑动监听
        setViewPagerListener();
        // 设置checkbox点击监听
        setCheckboxListener();
    }

    /**
     * 初始化标记容器
     */
    private void initSparseBooleanArray() {
        mFlags = new HashMap<>();
        for (int i = 0; i < mSelectionList.size(); i++) {
            mFlags.put(mSelectionList.get(i), true);
        }
    }


    @Override
    protected void initVariables() {
        mSelectionList = getIntent().getStringArrayListExtra("select_paths");
        // 初始化标记容器
        initSparseBooleanArray();
        // 初始化当前选择数
        mCurrNum = mSelectionList.size();
    }

    @Override
    protected void loadData() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 开启滑动菜单
                finish();
                break;
        }
        return true;
    }

    /**
     * 设置ViewPager滑动监听
     */
    private void setViewPagerListener() {
        mVpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAdapter.setCheek(position);
                if (mFlags.get(mSelectionList.get(position))) {
                    mCbSelection.setChecked(true);
                } else {
                    mCbSelection.setChecked(false);
                }
                mCurrPosition = position;
                String num = StringUtil.buildString(position + 1 + "", "/", mSelectionList.size() + "");
                mTvNum.setText(num);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置确认按钮的数字
     */
    private void setNumText(int currNum) {
        if (currNum == 0) {
            mBtnSure.setVisibility(View.GONE);
        } else {
            if (mBtnSure.getVisibility() == View.GONE) {
                mBtnSure.setVisibility(View.VISIBLE);
            }
            String numSure = StringUtil.buildString("确定", "(", currNum + "", ")");
            mBtnSure.setText(numSure);
        }
    }

    /**
     * 设置checkbox点击监听
     */
    private void setCheckboxListener() {
        mCbSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currPath = mSelectionList.get(mCurrPosition);
                if (mCbSelection.isChecked()) {
                    // 保存标记
                    mFlags.put(currPath, true);
                    // 去除滤镜
                    mAdapter.clearFilterOnItem(mCurrPosition);
                    // 确认数加1
                    setNumText(++mCurrNum);
                } else {
                    // 保存标记
                    mFlags.put(currPath, false);
                    // 添加滤镜
                    mAdapter.setFilterOnItem(mCurrPosition);
                    // 确认数减1
                    setNumText(--mCurrNum);
                }
            }
        });
    }

    /**
     * 控制顶部和底部的缩放
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void controlEdge() {
        if (mTbTop.getVisibility() == View.VISIBLE) {
            mTbTop.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRcvRotation.setVisibility(View.GONE);
        } else {
            mTbTop.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRcvRotation.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 初始化状态栏的颜色
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        PerViewViewPagerAdapter vpAdapter = new PerViewViewPagerAdapter(mSelectionList);
        vpAdapter.setViewPagerClick(new PerViewViewPagerAdapter.ViewPagerClickCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onViewPagerItemClick() {
                // 控制顶部和底部的缩放
                controlEdge();
            }
        });
        mVpContent.setAdapter(vpAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent i = new Intent("com.hebaiyi.www.katakuri.KatakuriActivity.freshSelection");
        i.putExtra("return_date", mFlags);
        sendBroadcast(i);
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        mAdapter = new PerViewBottomAdapter(mSelectionList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAdapter.setItemClickListener(new BaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mVpContent.setCurrentItem(position);
            }
        });
        mRcvRotation.setLayoutManager(manager);
        mRcvRotation.setAdapter(mAdapter);
    }


    /**
     * 设置ToolBar
     */
    private void setToolBar() {
        mTbTop.setTitle("");
        setSupportActionBar(mTbTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }


}
