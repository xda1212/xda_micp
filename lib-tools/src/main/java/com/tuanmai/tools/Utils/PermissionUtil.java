package com.tuanmai.tools.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuQiCong
 *
 * @date 2017-06-07 17:21
 * version 1.0
 * dsc 权限工具类
 */

public final class PermissionUtil {

    public static void autoGotoPermission(Context context) {
        context.startActivity(getAppDetailSettingIntent(context));
        /*if(gotoMiuiPermission(context)){
            //Utils.e("--------------Miui-------------------");
            ToastUtil.show(context,"若收不到推送消息,请允许通知类相关权限");
        }else if(gotoHuaweiPermission(context)){
            //Utils.e("---------------Huawei------------------");
            ToastUtil.show(context,"若收不到推送消息,请允许通知类相关权限");
        }else if(gotoMeizuPermission(context)){
            //Utils.e("--------------Meizu-------------------");
            ToastUtil.show(context,"若收不到推送消息,请允许通知类相关权限");
        }else{
            context.startActivity(getAppDetailSettingIntent(context));
        }*/
    }


    /**
     * 跳转到miui的权限管理页面
     */
    private static boolean gotoMiuiPermission(Context context) {
        try {
            //Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            //intent.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
            //上面跳到应用详情页面，非权限管理页面，无效
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 跳转到魅族的权限管理系统
     */
    private static boolean gotoMeizuPermission(Context context) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", context.getPackageName());
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 华为的权限管理页面
     */
    private static boolean gotoHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));

        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return intent;
    }

    public static final int REQUEST_NECESSARY_PERMISSION = 1;//必要权限申请request code
    public static final int REQUEST_OPTIONAL_PERMISSION = 2;//可选权限申请request code

    public static boolean checkPermission(Activity context, List<String> permissionList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissionList) {
                int permissionCode = ActivityCompat.checkSelfPermission(context, permission);
//                int permissionCode = context.checkSelfPermission(permission);
                if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestPermission(Activity context, int requestCode, List<String> permissionList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> noPermissionList = new ArrayList<>();
            for (String permission : permissionList) {
                int permissionCode = context.checkSelfPermission(permission);
                if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                    noPermissionList.add(permission);
                }
            }
            if (noPermissionList.size() > 0) {
                String[] permissionString = new String[]{};
                permissionString = noPermissionList.toArray(permissionString);
                ActivityCompat.requestPermissions(context,permissionString, requestCode);
//                context.requestPermissions(permissionString, requestCode);
            }
        }
    }
}
