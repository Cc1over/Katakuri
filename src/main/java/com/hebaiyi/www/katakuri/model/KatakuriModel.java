package com.hebaiyi.www.katakuri.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KatakuriModel {

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

    public List<String> scanPNGPicture(Context context){
       throw new NoSuchMethodError("no yet implements");
    }

    public List<String> scanJPGPicture(Context context){
        throw new NoSuchMethodError("no yet implements");
    }


}
