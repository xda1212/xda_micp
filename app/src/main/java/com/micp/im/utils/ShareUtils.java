package com.micp.im.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.micp.im.AppConfig;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.bean.SimpleUserInfo;
import com.tuanmai.tools.Utils.LogUtil;
import com.tuanmai.tools.toast.ToastUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/4/14.
 */
public class ShareUtils {
    public static void share(final Activity context, final int index, final SimpleUserInfo user) {
        PhoneLiveApi.getConfig(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(context, "获取分享地址失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (res != null) {
                    try {
                        JSONObject jsonObject = res.getJSONObject(0);
                        String shareUrl = jsonObject.getString("app_android");

                        SHARE_MEDIA names = SHARE_MEDIA.QQ;
                        if (AppConfig.SHARE_TYPE.getString(index).equals("qq")) {
                            names = SHARE_MEDIA.QQ;
                        } else if (AppConfig.SHARE_TYPE.getString(index).equals("qzone")) {
                            names = SHARE_MEDIA.QZONE;
                        } else if (AppConfig.SHARE_TYPE.getString(index).equals("wchat")) {
                            shareUrl = jsonObject.getString("wx_siteurl") + user.id;
                            names = SHARE_MEDIA.WEIXIN_CIRCLE;
                        } else if (AppConfig.SHARE_TYPE.getString(index).equals("wx")) {
                            shareUrl = jsonObject.getString("wx_siteurl") + user.id;
                            names = SHARE_MEDIA.WEIXIN;
                        }

                        share(context, names, jsonObject.getString("share_title")
                                , user.user_nicename + jsonObject.getString("share_des"), user, null, shareUrl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public static void share(final Activity context, final SHARE_MEDIA type, final SimpleUserInfo user) {
        PhoneLiveApi.getConfig(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(context, "获取分享地址失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (res != null) {
                    try {
                        JSONObject jsonObject = res.getJSONObject(0);
                        String shareUrl = jsonObject.getString("wx_siteurl") + user.id;
                        share(context, type, jsonObject.getString("share_title")
                                , user.user_nicename + jsonObject.getString("share_des"), user, null, shareUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    //    public static void share(final Context context, String name, String title, String des, final SimpleUserInfo user, String thumb_s, String shareUrl, PlatformActionListener listener) {
//        MobSDK.init(context);
//        final OnekeyShare oks = new OnekeyShare();
//        oks.setSilent(true);
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        oks.setPlatform(name);
//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(title);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(des);
//        if (user != null) {
//            oks.setImageUrl(user.avatar_thumb);
//        } else {
//            oks.setImageUrl(thumb_s);
//        }
//
//
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        if (name.equals(Wechat.NAME) || name.equals(WechatMoments.NAME)) {
//            oks.setUrl(shareUrl);
//            oks.setSiteUrl(shareUrl);
//            oks.setTitleUrl(shareUrl);
//
//        } else {
//            oks.setUrl(shareUrl);
//            oks.setSiteUrl(shareUrl);
//            oks.setTitleUrl(shareUrl);
//        }
//
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        //oks.setComment(context.getString(R.string.shartitle));
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(context.getString(R.string.app_name));
//        oks.setCallback(listener);
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//
//        // 启动分享GUI
//        oks.show(context);
//    }
    public static void share(final Activity context, SHARE_MEDIA type, String title, String des, final SimpleUserInfo user, String thumb_s, String shareUrl) {
        ShareAction shareAction = new ShareAction(context);
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                LogUtil.e(share_media.toString());
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });//回调监听器
        shareAction.setPlatform(type);//传入平台
        UMWeb umWeb = new UMWeb(shareUrl);
        umWeb.setTitle(title);
        umWeb.setDescription(des);
        if (!TextUtils.isEmpty(thumb_s)) {
            UMImage umImage = new UMImage(context, thumb_s);
//            shareAction.withMedia(umImage);
            umWeb.setThumb(umImage);
        }
        shareAction.withMedia(umWeb);//分享内容
        shareAction.share();
    }
}