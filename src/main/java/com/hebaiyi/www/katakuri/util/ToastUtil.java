package com.hebaiyi.www.katakuri.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mToast = null;

    public static void showToast(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }

        mToast.show();
    }
}
