package com.tuanmai.tools.toast;

import android.content.Context;
import android.os.Build;
import android.view.View;

/**
 * Created by ttt on 2016/7/5.
 */
public final class ToastCompat implements IToast {

    private static int checkNotification = -1;

    private IToast mIToast;

    public ToastCompat(Context context) {
        this(context, null, -1);
    }

    public ToastCompat(Context context, String text, int duration) {
        if (checkNotification == -1) {
            checkNotification = isNotificationEnabled(context) ? 0 : 1;
        }
        if ((Build.VERSION.SDK_INT >= 19)) {
            if (checkNotification == 1) {
                mIToast = CustomToast.makeText(context, text, duration);
            } else {
                mIToast = SystemToast.makeText(context, text, duration);
            }
        } else {
            mIToast = SystemToast.makeText(context, text, duration);
        }
    }

    public static IToast makeText(Context context, String text, int duration) {
        return new ToastCompat(context, text, duration);
    }

    @Override
    public IToast setGravity(int gravity, int xOffset, int yOffset) {
        return mIToast.setGravity(gravity, xOffset, yOffset);
    }

    @Override
    public IToast setDuration(long durationMillis) {
        return mIToast.setDuration(durationMillis);
    }

    /**
     * 不能和{@link #setText(String)}一起使用，要么{@link #setView(View)} 要么{@link #setView(View)}
     *
     * @param view
     */
    @Override
    public IToast setView(View view) {
        return mIToast.setView(view);
    }

    @Override
    public IToast setMargin(float horizontalMargin, float verticalMargin) {
        return mIToast.setMargin(horizontalMargin, verticalMargin);
    }

    /**
     * 不能和{@link #setView(View)}一起使用，要么{@link #setView(View)} 要么{@link #setView(View)}
     *
     * @param text
     */
    @Override
    public IToast setText(String text) {
        return mIToast.setText(text);
    }

    @Override
    public void show() {
        mIToast.show();
    }

    @Override
    public void cancel() {
        mIToast.cancel();
    }

    public static boolean isNotificationEnabled(Context context) {
        return NotificationsUtils.isNotificationEnabled(context);
    }
}
