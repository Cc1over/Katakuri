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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.hebaiyi.www.katakuri.Config;
import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.adapter.BaseAdapter;
import com.hebaiyi.www.katakuri.adapter.PerViewBottomAdapter;
import com.hebaiyi.www.katakuri.adapter.PerViewViewPagerAdapter;
import com.hebaiyi.www.katakuri.util.StringUtil;
import com.hebaiyi.www.katakuri.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hebaiyi.www.katakuri.activity.KatakuriActivity.EXTRA_NAME;

public class ImageActivity extends BaseActivity {

    public static int REQUEST_CODE = 96;
    private static final int MAX_SELECT_NUM = Config.getInstance().getMaxSelectable();

    private Toolbar mTbTop;
    private List<String> mSelectionList;
    private List<String> mAllPaths;
    private ViewPager mVpContent;
    private RecyclerView mRcvRotation;
    private CheckBox mCbSelection;
    private Button mBtnSure;
    private TextView mTvNum;
    private PerViewBottomAdapter mAdapter;
    private HashMap<String, Boolean> mFlags; // 标记是否选择容器
    private int mCurrPosition; // 当前索引
    private int mCurrNum; // 当前选择数

    /**
     *  启动活动
     * @param activity 发出指令的活动
     * @param paths 选中项的地址
     * @param allPaths 文件夹全部地址
     * @param currPosition 当前选择项索引
     */
    public static void actionStart(Activity activity, List<String> paths, List<String> allPaths, int currPosition) {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putStringArrayListExtra("selection_paths", (ArrayList<String>) paths);
        intent.putStringArrayListExtra("all_paths", (ArrayList<String>) allPaths);
        intent.putExtra("currPosition", currPosition);
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
        if (mSelectionList.size() == 0) {
            mBtnSure.setVisibility(View.GONE);
        }
        String numSure = StringUtil.buildString("确定", "(", mSelectionList.size() + "", ")");
        mBtnSure.setText(numSure);
        // 设置数字标题
        String num = StringUtil.buildString(mCurrPosition + 1 + "", "/", mAllPaths.size() + "");
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
        // 设置监听
        mBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putStringArrayListExtra(EXTRA_NAME, (ArrayList<String>) mSelectionList);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    @Override
    protected void initVariables() {
        mSelectionList = getIntent().getStringArrayListExtra("selection_paths");
        mAllPaths = getIntent().getStringArrayListExtra("all_paths");
        // 初始化标记容器
        initHashMap();
        // 初始化当前选择数
        mCurrNum = mSelectionList.size();
        // 初始化当前位置
        mCurrPosition = getIntent().getIntExtra("currPosition", 0);
    }

    @Override
    protected void loadData() {

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
     * 初始化标记容器
     */
    private void initHashMap() {
        mFlags = new HashMap<>();
        for (int i = 0; i < mSelectionList.size(); i++) {
            mFlags.put(mSelectionList.get(i), true);
        }
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
                String currPath = mAllPaths.get(mCurrPosition);
                if (mCbSelection.isChecked()) {
                    if (mCurrNum == MAX_SELECT_NUM) {
                        ToastUtil.showToast(ImageActivity.this,
                                "最多只能选择" + MAX_SELECT_NUM + "项", Toast.LENGTH_SHORT);
                        mCbSelection.setChecked(false);
                        return;
                    }
                    // 保存标记
                    mFlags.put(currPath, true);
                    // 判空处理
                    if (mAdapter == null) {
                        mSelectionList.add(currPath);
                        initRecyclerView();
                    } else {
                        // 添加选择项
                        mAdapter.addItem(currPath);
                        // 设置底部图片边框
                        mAdapter.setCheek(mSelectionList.indexOf(currPath));
                        // 移动到最后位置
                        mRcvRotation.scrollToPosition(mSelectionList.size() - 1);
                    }
                    // 确认数加1
                    setNumText(++mCurrNum);
                } else {
                    // 保存标记
                    mFlags.put(currPath, false);
                    // 去除元素
                    mAdapter.deleteItem(mSelectionList.indexOf(currPath));
                    // 确认数减1
                    setNumText(--mCurrNum);
                }
            }
        });
    }

    /**
     * 判断当前页是否在被选择
     *
     * @return 是否存在
     */
    private boolean isSelect() {
        if (mSelectionList.size() == 0) {
            return false;
        }
        String currPath = mAllPaths.get(mCurrPosition);
        for (int i = 0; i < mSelectionList.size(); i++) {
            if (currPath.equals(mSelectionList.get(i))) {
                return true;
            }
        }
        return false;
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
                // 保存位置
                mCurrPosition = position;
                // 设置checkbox
                setCheckbox();
                String num = StringUtil.buildString(mCurrPosition + 1 + "", "/", mAllPaths.size() + "");
                mTvNum.setText(num);
                if(mAdapter==null){
                    return;
                }
                if (isSelect()) {
                    // 设置底部图片边框
                    mAdapter.setCheek(mSelectionList.indexOf(mAllPaths.get(position)));
                } else {
                    // 隐藏底部图片边框
                    mAdapter.hideCheek();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
     * 控制顶部和底部的缩放
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void controlEdge() {
        if (mTbTop.getVisibility() == View.VISIBLE) {
            mTbTop.setVisibility(View.GONE);
            // 隐藏状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRcvRotation.setVisibility(View.GONE);
        } else {
            mTbTop.setVisibility(View.VISIBLE);
            // 显示状态栏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRcvRotation.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        PerViewViewPagerAdapter vpAdapter = new PerViewViewPagerAdapter(mAllPaths);
        vpAdapter.setViewPagerClick(new PerViewViewPagerAdapter.ViewPagerClickCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onViewPagerItemClick() {
                // 控制顶部和底部的缩放
                controlEdge();
            }
        });
        mVpContent.setAdapter(vpAdapter);
        mVpContent.setCurrentItem(mCurrPosition);
        // 设置checkbox
        setCheckbox();
    }

    /**
     * 根据当前情况设置checkbox
     */
    private void setCheckbox() {
        if (isSelect()) {
            mCbSelection.setChecked(true);
        } else {
            mCbSelection.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 发送广播通知主界面改变
        Intent i = new Intent("com.hebaiyi.www.katakuri.KatakuriActivity.freshSelection");
        i.putExtra("return_date", mFlags);
        sendBroadcast(i);
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        // 判断是否有选择项
        if (mSelectionList.size() == 0) {
            return;
        }
        // 判断adapter是否经过初始化
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        mAdapter = new PerViewBottomAdapter(mSelectionList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAdapter.setItemClickListener(new BaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String path = mSelectionList.get(position);
                int index = mAllPaths.indexOf(path);
                if (index == -1) {
                    ToastUtil.showToast(ImageActivity.this, "该文件夹没有该项", Toast.LENGTH_SHORT);
                    return;
                }
                mVpContent.setCurrentItem(index);
                mAdapter.setCheek(position);
                // 设置checkbox
                setCheckbox();
            }
        });
        mRcvRotation.setLayoutManager(manager);
        mRcvRotation.setAdapter(mAdapter);
        // 隐藏边框
        mAdapter.hideCheek();
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
