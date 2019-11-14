package com.tuanmai.tools.Utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.tuanmai.tools.toast.ToastUtil;

import java.util.List;

/**
 * Created by LiuQiCong
 * date 2017-12-22 10:51
 * version 1.0
 * dsc 工具方法集合
 */
public final class ToolUtil {

    /**
     * 去往应用市场打评分
     */
    public static void gotoRate(Context context){
        if(null!=context){
            if(hasAnyMarketInstalled(context)){
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(goToMarket);
                    return;
                } catch (ActivityNotFoundException e) {}
            }
            ToastUtil.showShort(context,"尚未安装应用市场，无法评分");
        }
    }


    /**
     * 是否安装了应用市场
     */
    public static boolean hasAnyMarketInstalled(Context context) {
        if(null!=context){
            Intent intent =new Intent();
            intent.setData(Uri.parse("market://details?id=android.browser"));
            List list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return 0!= list.size();
        }
        return false;
    }


}
