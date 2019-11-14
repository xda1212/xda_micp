package com.tuanmai.tools.toast;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationsUtils {

//    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
//    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    /**
     * 权限是否已经开启
     */
    public static boolean isNotificationEnabled(Context context) {

        //added in version 24.1.0
        return NotificationManagerCompat.from(context).areNotificationsEnabled();

        /*if (Build.VERSION.SDK_INT >= 19) {
            String pkg = context.getApplicationContext().getPackageName();
            int uid = context.getApplicationInfo().uid;
            try {
                AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                Class appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                        String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

                int value = (Integer) opPostNotificationValue.get(Integer.class);
                return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;*/
    }
}