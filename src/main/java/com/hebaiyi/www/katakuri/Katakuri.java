package com.hebaiyi.www.katakuri;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

public final class Katakuri {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private Katakuri(Activity activity) {
        mContext = new WeakReference<>(activity);
        mFragment = null;
    }

    private Katakuri(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static Katakuri from(Activity activity) {
        return new Katakuri(activity);
    }

    public static Katakuri from(Fragment fragment) {
        return new Katakuri(fragment.getActivity(), fragment);
    }

    public ConfigBuilder choose(ImageType type) {
        return new ConfigBuilder(this, type);
    }

    Activity getActivity() {
        return mContext.get();
    }

    Fragment getFragment() {
        if (mFragment == null) {
            return null;
        } else {
            return mFragment.get();
        }
    }

    public enum ImageType {

        PNG, JPEG, ALL

    }

}
