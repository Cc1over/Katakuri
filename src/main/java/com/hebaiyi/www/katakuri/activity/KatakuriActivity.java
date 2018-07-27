package com.hebaiyi.www.katakuri.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hebaiyi.www.katakuri.R;
import com.hebaiyi.www.katakuri.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KatakuriActivity extends AppCompatActivity {

    private RecyclerView mRcvContent;
    private List<String> childPath = new ArrayList<>();
    private Toolbar mTbTop;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katakuri);
        mRcvContent = findViewById(R.id.katakuri_rcv_content);
        mTbTop = findViewById(R.id.katakuri_tb_top);
        // 设置状态栏颜色
        setStatusBarColor();
        // 初始化Toolbar
        initToolbar();
        // 请求权限
        requestPermission();
        // 初始化列表
        initList();
    }

    private void initToolbar() {
        mTbTop.setTitle("");
        setSupportActionBar(mTbTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }

    /**
     * 初始化列表
     */
    private void initList() {
        ImageAdapter adapter = new ImageAdapter(childPath);
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
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = KatakuriActivity.this.getContentResolver();
        //只查询jpeg和png的图片
        Cursor cursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            childPath.add(path);
        }
        Collections.reverse(childPath);
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

}
