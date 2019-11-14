package com.tuanmai.tools.Utils.app;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.tuanmai.tools.Utils.PreferencesUtil;
import com.tuanmai.tools.toast.ToastUtil;

import java.util.Random;

public final class AndroidDevice {

    private static String mVersonName = null;
    private static int mVersonCode = 0;

    /**
     * SD卡是否可用
     *
     * @return
     */
    public static boolean isSDCardAvaiable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * IMEI号，标识网络中唯一的手机，存储在手机中
     */
    public static final String getDeviceId(final Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return manager != null ? manager.getDeviceId() : null;
    }

    /**
     * IMSI号，标识网络中唯一的用户，存储在SIM卡中
     */
    public static final String getIMSI(final Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return manager != null ? manager.getSubscriberId() : null;
    }

    /**
     * IMEI码
     */
    public static final String getIMEI(final Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String ITEM = manager != null ? manager.getDeviceId() : null;
        if (ITEM == null) {
            ITEM = "";
        }
        return ITEM;
    }

    /**
     * IMEI码,用于传递给h5的vid参数,获取不到用本地保存的
     * 拼接在url后面,传递给h5的参数vid
     * String URL_PARAM_VID = "url_param_vid";
     */
    public static final String getIMEI4Vid(final Context context) {
        String vid = PreferencesUtil.getString("url_param_vid", "");
        if (!TextUtils.isEmpty(vid)){
            return vid;
        }else {
            vid = getIMEI(context);
            if (TextUtils.isEmpty(vid)){
                vid = getLocalVidStr();
            }
            PreferencesUtil.putString("url_param_vid",vid);
            return vid;
        }
    }

    private static final String getLocalVidStr() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 5; i++) {
            result += random.nextInt(10);
        }
        long l = System.currentTimeMillis() / 1000;
        return l + result;
    }

    /**
     * 获取Sim卡串号
     *
     * @param context
     * @return
     */
    public static final String getSimSerialNum(final Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String uuid = manager != null ? manager.getSimSerialNumber() : null;
        if (uuid == null) {
            uuid = "";
        }
        return uuid;
    }

    /**
     * 自动弹出软 键盘
     *
     * @param etID
     * @date 2013-10-25 下午3:18:23
     * @returnType void
     */
    public static void showSoftkeyboard(final EditText etID, final Context mContext) {
        if (null != etID) {
            etID.post(new Runnable() {
                @Override
                public void run() {
                    if (null != etID && null != mContext) {
                        etID.requestFocus(etID.getText().length());
                        InputMethodManager imm = (InputMethodManager) mContext
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        // imm.showSoftInput(etID, 0);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
            });

        }
    }

    /**
     * 隐藏软键盘
     *
     * @date 2013-10-27 上午10:51:36
     * @returnType void
     */
    public static void hideSoftkeyboard(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 获取SD卡总容量
     *
     * @return
     */
    public static final long getSDCardTotalSize() {
        String filePath = AndroidDevice.getExternalStoragePath();
        if (filePath == null) {
            return 0;
        }
        StatFs statFs = new StatFs(filePath);
        // 获取block的SIZE
        long blocSize = statFs.getBlockSize();
        // 获取BLOCK数量
        long totalBlocks = statFs.getBlockCount();
        long total = totalBlocks * blocSize;
        return total;
    }

    /**
     * 获取SD卡可用空间大小
     *
     * @return
     */
    public static final long getSDCardAvailableSize() {
        String filePath = AndroidDevice.getExternalStoragePath();
        if (filePath == null) {
            return 0;
        }
        StatFs statFs = new StatFs(filePath);
        // 获取block的SIZE
        long blocSize = statFs.getBlockSize();
        // 可使用的Block的数量
        long availaBlock = statFs.getAvailableBlocks();
        long availableSpare = availaBlock * blocSize;
        return availableSpare;
    }

    /**
     * 获取存储卡的路径
     *
     * @return
     */
    public static final String getExternalStoragePath() {
        // 获取SdCard状态
        String state = Environment.getExternalStorageState();
        // 判断SdCard是否存在并且是可读或可写可读的
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return Environment.getExternalStorageDirectory()
                    .getPath();
        }
        return null;
    }


    /**
     * 网络是否可以用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return 剩余内存
     */
    public static String getAvailMemory(Context mContext) {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        ToastUtil.show(mContext, Formatter.formatFileSize(mContext, mi.availMem));
        return Formatter.formatFileSize(mContext, mi.availMem);// 将获取的内存大小规格化
    }
}
