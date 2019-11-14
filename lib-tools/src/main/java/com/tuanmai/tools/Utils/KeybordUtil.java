package com.tuanmai.tools.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LiuQiCong
 *
 * @date 2017-06-07 17:08
 * version 1.0
 * dsc 键盘工具类
 */
public final class KeybordUtil {


    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideSoftKeybord(Activity activity) {
        if (null == activity) {
            return;
        }
        try {
            final View v = activity.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 隐藏虚拟键盘
    public static void hideSoftKeybord(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }

    // 显示虚拟键盘
    public static void showSoftKeybord(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
        }

    }

    // 强制显示或者关闭系统键盘
    public static void handleKeyboard(final View v, final boolean show) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (show) {
                    showSoftKeybord(v);
                } else {
                    hideSoftKeybord(v);
                }
            }
        }, 100);
    }

    // 定时器强制隐藏虚拟键盘
    public static void hideKeyboardOnTime(final View v) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                hideSoftKeybord(v);
            }
        }, 10);
    }

    // 输入法是否显示着
    public static boolean handleKeyboard(View v) {
        boolean bool = false;
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            bool = true;
        }
        return bool;

    }
}
