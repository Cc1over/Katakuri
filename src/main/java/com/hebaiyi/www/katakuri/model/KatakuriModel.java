package com.hebaiyi.www.katakuri.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.hebaiyi.www.katakuri.bean.Folder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KatakuriModel {

    private static int SELECT_PNG = 11;
    private static int SELECT_ALL = 23;
    private static int SELECT_JPEG = 33;

    public List<String> scanAllPicture(Context context) {
        List<String> childPath = new ArrayList<>();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
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
        return childPath;
    }

    /**
     * 异步扫描缩略图片
     *
     * @param context  上下文
     * @param callback 回调接口
     */
    public void scan(final Context context, final KatakuriModelCallback callback) {
        if (callback == null) {
            throw new NullPointerException("callback must no be null");
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 失败回调
            callback.onFail();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 查询cursor
                Cursor cursor = getScanCursor(context, SELECT_ALL);
                if (cursor == null) {
                    callback.onFail();
                    return;
                }
                // 创建容器
                List<Folder> folderList = new ArrayList<>();
                List<String> paths = new ArrayList<>();
                // 遍历地址获取文件夹
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    // 保存子地址
                    paths.add(path);
                    File parentFile = new File(path).getParentFile();
                    String parentPath = parentFile.getAbsolutePath();
                    // 对文件夹判空
                    if (parentFile == null) {
                        continue;
                    }
                    // 数据封装
                    Set<String> directory = new HashSet<>();
                    Folder folder;
                    // 检查该文件夹是否遍历过
                    if (directory.contains(parentPath)) {
                        continue;
                    } else {
                        directory.add(parentPath);
                        folder = new Folder();
                        folder.setFolderName(parentPath);
                        folder.setFirstImagePath(path);
                    }
                    // 判空处理
                    if (parentFile.list() == null) {
                        continue;
                    }
                    // 获取图片数量
                    int imageNum = getImageCount(parentFile);
                    // 设置图片数量
                    folder.setImageNum(imageNum);
                    // 添加到容器中
                    folderList.add(folder);
                }
                // 扫描完成
                callback.onSuccess(folderList, paths);
                cursor.close();
            }
        }).start();
    }

    /**
     * 获取父目录中图片的属相
     *
     * @param parentFile 父目录
     * @return 图片数量
     */
    private int getImageCount(File parentFile) {
        return parentFile.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
            }
        }).length;
    }

    /**
     * 根据扫描类型去获取扫描图片的游标
     *
     * @param context    上下文
     * @param selectType 扫描类型
     * @return 游标
     */
    private Cursor getScanCursor(Context context, int selectType) {
        // 获取扫描约束
        String selection = getScanSelection(selectType);
        // 获取扫描具体条件
        String[] selectionArgs = getScanSelectionArgs(selectType);
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        return mContentResolver.query(mImageUri, null,
                selection, selectionArgs, MediaStore.Images.Media.DATE_MODIFIED);
    }

    /**
     * 获取扫描用到的选择约束
     *
     * @param selectType 选择类型
     * @return 选择约束
     */
    private String getScanSelection(int selectType) {
        if (selectType == SELECT_ALL) {
            return MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=?";
        }
        if (selectType == SELECT_JPEG || selectType == SELECT_PNG) {
            return MediaStore.Images.Media.MIME_TYPE + "=? ";
        }
        throw new IllegalArgumentException("selectType error");
    }

    /**
     * 获取选择用到具体条件
     *
     * @param selectType 选择类型
     * @return 具体条件
     */
    private String[] getScanSelectionArgs(int selectType) {
        if (selectType == SELECT_ALL) {
            return new String[]{"image/jpeg", "image/png"};
        }
        if (selectType == SELECT_JPEG) {
            return new String[]{"image/jpeg"};
        }
        if (selectType == SELECT_PNG) {
            return new String[]{"image/png"};
        }
        throw new IllegalArgumentException("selectType error");
    }

    public List<String> scanPNGPicture(Context context) {
        throw new NoSuchMethodError("no yet implements");
    }

    public List<String> scanJPGPicture(Context context) {
        throw new NoSuchMethodError("no yet implements");
    }

    public interface KatakuriModelCallback {

        void onSuccess(List<Folder> folders, List<String> paths);

        void onFail();

    }

}
