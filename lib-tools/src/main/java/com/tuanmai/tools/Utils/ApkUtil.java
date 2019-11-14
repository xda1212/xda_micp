package com.tuanmai.tools.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by LiuQiCong
 * date 2017-12-07 17:24
 * version 1.0
 * dsc apkb包工具类
 */
public final class ApkUtil {


    /**
     * 从一个apk文件去获取该文件的版本信息
     *
     * @param context
     *            本应用程序上下文
     * @param archiveFilePath
     *            APK文件的路径。如：/sdcard/download/XX.apk
     * @return
     */
    public static String getVersionNameFromApk(Context context, String archiveFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        return packInfo.versionName;
    }


    /**
     * 从一个apk文件去获取该文件的版本信息
     *
     * @param context
     *            本应用程序上下文
     * @param archiveFilePath
     *            APK文件的路径。如：/sdcard/download/XX.apk
     * @return
     */
    public static int getVersionCodeFromApk(Context context, String archiveFilePath) {
        try{
            PackageManager pm = context.getPackageManager();
            PackageInfo packInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
            return packInfo.versionCode;
        }catch (Exception e){
            return -1;
        }
    }

}
