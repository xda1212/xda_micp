package com.tuanmai.tools.toast;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tuanmai.tools.R;
import com.tuanmai.tools.Utils.ResUtil;
import com.tuanmai.tools.Utils.ScreenUtil;

/**
 * 自定义Toast
 */
public final class ToastUtil {

    private static String LAST_CONTENT = "";
    private static long LAST_TIME = 0L;

    /**
     * 显示内容和上一次雷同且5s内不显示
     */
    private static boolean isEnable(String content) {
        if (LAST_CONTENT.equals(content) && System.currentTimeMillis() - LAST_TIME < 5000) {
            return false;
        }
        LAST_CONTENT = content;
        LAST_TIME = System.currentTimeMillis();
        return true;
    }

    @UiThread
    public static void show(Context context, int strid) {
        show(context, ResUtil.getString(strid));
    }

    @UiThread
    public static void show(Context context, String content) {
        if (null != context && !TextUtils.isEmpty(content)) {
            if (!isEnable(content)) return;
            //权限是否开启
            if (NotificationsUtils.isNotificationEnabled(context)) {
                showSystemToast(context, content, Toast.LENGTH_LONG);
            } else {
                showCustomToast(context, content, Toast.LENGTH_LONG);
            }

        }
    }


    @UiThread
    public static void showShort(Context context, int strid) {
        showShort(context, ResUtil.getString(strid));
    }

    @UiThread
    public static void showShort(Context context, String content) {
        if (null == context || TextUtils.isEmpty(content)) return;
        if (!isEnable(content)) return;
        //权限是否开启
        if (NotificationsUtils.isNotificationEnabled(context)) {
            showSystemToast(context, content, Toast.LENGTH_SHORT);
        } else {
            showCustomToast(context, content, Toast.LENGTH_SHORT);
        }
    }

    @UiThread
    private static void showSystemToast(Context context, String content, int duration) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.tools_layout_custom_toast, null);
            TextView tvContent = layout.findViewById(R.id.tv_toast_content);
            tvContent.setText(content);

            Toast toast = new Toast(context);
            //toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setDuration(duration);
            toast.setView(layout);

            if (Build.VERSION.SDK_INT == 25) {
                WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
                mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mParams.format = PixelFormat.TRANSLUCENT;
                mParams.windowAnimations = android.R.style.Animation_Toast;
                mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    private static void showCustomToast(final Context context, String content, int duration) {

        if (Build.VERSION.SDK_INT > 24) {
            if (context instanceof Activity) {
                EToast2.makeText(context, content, duration).show();
            }
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.tools_layout_custom_toast, null);
            TextView tvContent = layout.findViewById(R.id.tv_toast_content);
            tvContent.setText(content);
            ToastCompat toast = new ToastCompat(context.getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, ScreenUtil.getScreenHeight() / 9);
            toast.setDuration(duration);
            toast.setView(layout);
            toast.show();
        }
    }


}
