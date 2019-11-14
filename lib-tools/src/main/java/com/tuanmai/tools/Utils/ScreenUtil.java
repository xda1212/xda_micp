package com.tuanmai.tools.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by LiuQiCong
 *
 * @date 2017-06-07 15:31
 * version 1.0
 * dsc 描述
 */


public class ScreenUtil {

    private static Context mContext;

    public static void init(Context context){
        mContext=context.getApplicationContext();
    }

    // ---------------------------------------------------------------------------------
    /**
     * 功能：屏幕单位转换
     */
    public static int px2dp(float pxValue) {
        if(null!=mContext){
            return (int) (pxValue / mContext.getResources().getDisplayMetrics().density + 0.5f);
        }
        return 0;
    }

    /**
     * 功能：屏幕单位转换
     */
    public static int dp2px(float dpValue) {
        if(null!=mContext && dpValue>0){
            return (int) (dpValue * mContext.getResources().getDisplayMetrics().density + 0.5f);
        }
        return 0;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        if(null!=mContext && pxValue>0){
            return (int) (pxValue / mContext.getResources().getDisplayMetrics().scaledDensity + 0.5f);
        }
        return 0;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue) {
        if(null!=mContext && spValue>0){
            return (int) (spValue * mContext.getResources().getDisplayMetrics().scaledDensity + 0.5f);
        }
        return 0;
    }


    // ---------------------------------------------------------------------------------
    private static int mWidthPixels;
    private static int mHeightPixels;
    /**
     * 功能：获得屏幕宽度（px）
     */
    public static int getScreenWidth() {
        if(mWidthPixels<=0 && null!=mContext){
            mWidthPixels=mContext.getResources().getDisplayMetrics().widthPixels;
        }
        return mWidthPixels;
    }

    /**
     * 功能：获得屏幕高度（px）
     */
    public static int getScreenHeight() {
        if(mHeightPixels<=0 && null!=mContext){
            mHeightPixels=mContext.getResources().getDisplayMetrics().heightPixels;
        }
        return mHeightPixels;
    }

    /**
     * 获得屏幕宽度（px）
     */
    public static int getScreenWidth(WindowManager manager) {
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * 获得屏幕高度（px）
     */
    public static int getScreenHeight(WindowManager manager) {
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }


    // ---------------------------------------------------------------------------------
    /**
     * 获得屏幕比例
     */
    public static float getAreaOneRatio(Activity activity){
        int[] size = getAreaOne(activity);
        int a = size[0];
        int b = size[1];
        float ratio = 0f;
        if (a > b) {
            if(b!=0){
                ratio = (float) a / (float) b;
            }
        } else {
            if(a!=0){
                ratio = (float) b / (float) a;
            }
        }
        return ratio;
    }



    // ---------------------------------------------------------------------------------
    /**
     * 获得屏幕尺寸（宽 x 高）（px）
     */
    public static int[] getAreaOne(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return new int[]{ point.x, point.y };
    }

    /**
     * 获得应用区域尺寸（宽 x 高）（px）
     */
    public static int[] getAreaTwo(Activity activity){
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return new int[]{ rect.width(), rect.height() };
    }

    /**
     * 获得用户绘图区域尺寸（宽 x 高）（px）
     */
    public static int[] getAreaThree(Activity activity){
        Rect rect = new Rect();
        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(rect);
        return new int[]{ rect.width(), rect.height() };
    }



}
