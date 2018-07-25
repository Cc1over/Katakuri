package com.hebaiyi.www.katakuri;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hebaiyi.www.katakuri.activity.KatakuriActivity;
import com.hebaiyi.www.katakuri.engine.ImageEngine;

public final class ConfigBuilder {

    private Katakuri mKatakuri;
    private Config mConfig;

    ConfigBuilder(Katakuri katakuri, Katakuri.ImageType type) {
        mKatakuri = katakuri;
        mConfig = Config.getDefaultInstance();
        mConfig.imageType = type;
    }

    public ConfigBuilder maxSelectable(int maxSelectable) {
        mConfig.maxSelectable = maxSelectable;
        return this;
    }

    public ConfigBuilder imageEngine(ImageEngine engine) {
        mConfig.mImageEngine = engine;
        return this;
    }

    public ConfigBuilder thumbnailScale(float thumbnailScale) {
        mConfig.thumbnailScale = thumbnailScale;
        return this;
    }

    public void forResult(int requestCode) {
        Activity activity = mKatakuri.getActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, KatakuriActivity.class);
            Fragment fragment = mKatakuri.getFragment();
            if(fragment!=null){
                fragment.startActivityForResult(intent,requestCode);
            }else{
                activity.startActivityForResult(intent,requestCode);
            }
        }
    }

}
