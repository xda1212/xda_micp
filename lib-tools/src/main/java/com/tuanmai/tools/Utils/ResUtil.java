package com.tuanmai.tools.Utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by LiuQiCong
 *
 * @date 2017-07-20 11:13
 * version 1.0
 * dsc 资源获取工具类
 */
public final class ResUtil {

    private static Context mContext;

    public static void init(Context context){
        mContext=context.getApplicationContext();
    }


    //=======================================资源获取相关===========================================
    public static String getString(@StringRes int stringID, Object... objects) {
        if (null != mContext) {
            return mContext.getString(stringID, objects);
        }
        return null;
    }

    @ColorInt
    public static int getColor(@ColorRes int resID) {
        if (null != mContext) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mContext.getResources().getColor(resID, mContext.getTheme());
            } else {
                return mContext.getResources().getColor(resID);
            }
        }
        return 0;
    }

    public static ColorStateList getColorStateList(@ColorRes int resID){
        if (null != mContext) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mContext.getResources().getColorStateList(resID, mContext.getTheme());
            } else {
                return mContext.getResources().getColorStateList(resID);
            }
        }
        return null;
    }

    public static Drawable getDrawable(@DrawableRes int resID){
        if (null != mContext) {
            return  mContext.getResources().getDrawable(resID);
        }
        return null;
    }

    public static String[] getStringArray(@ArrayRes int resID){
        if (null != mContext) {
            return  mContext.getResources().getStringArray(resID);
        }
        return new String[0];
    }

    public static int getDimensionPixelSize(@DimenRes int resID){
        if (null != mContext) {
            return mContext.getResources().getDimensionPixelSize(resID);
        }
        return 0;
    }

}
