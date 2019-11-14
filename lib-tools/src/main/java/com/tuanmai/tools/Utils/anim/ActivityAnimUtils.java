package com.tuanmai.tools.Utils.anim;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

/**
 * Created by LiuQiCong
 *
 * @date 2017-04-24 19:36
 * version 1.0
 * dsc activity过度动画工具类
 */
public final class ActivityAnimUtils {

    public static void startActivity(Activity activity, Intent intent, View view){
        // 5.0才支持转场动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !android.os.Build.BRAND.contains("HUAWEI")) {
            Bundle tranBundle = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity, view,"goodsTrans")
                    .toBundle();
            activity.startActivity(intent, tranBundle);
        } else {
            activity.startActivity(intent);
        }
    }

}
