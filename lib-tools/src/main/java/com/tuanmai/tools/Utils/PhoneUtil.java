package com.tuanmai.tools.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;

import com.tuanmai.tools.toast.ToastUtil;

/**
 * Created by LiuQiCong
 *
 * @date 2017-05-03 17:44
 * version 1.0
 * dsc 电话工具类
 */

public final class PhoneUtil {

    public static void callPhone(Context context, String number){
        try{
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ number));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch(Exception e){
            ToastUtil.showShort(context,"权限不足或号码有误~~");
        }
    }

	public static void sendSMS(Context context,String phoneNumber,String message){
		if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));
			intent.putExtra("sms_body", message);
			context.startActivity(intent);
		}
	}

}
