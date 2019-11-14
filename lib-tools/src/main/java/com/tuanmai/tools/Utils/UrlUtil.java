package com.tuanmai.tools.Utils;

import android.text.TextUtils;

/**
 * Created by LiuQiCong
 *
 * @date 2017-07-17 09:17
 * version 1.0
 * dsc 描述
 */
public final class UrlUtil {

    public static String getUrl(String url) {
        if (null != url) {
            if (url.startsWith("http")) {
                return url;
            } else {
                if (url.startsWith("://")) {
                    return "https" + url;
                } else if (url.startsWith("//")) {
                    return "https:" + url;
                }
                return "https://" + url;
            }
        } else {
            return "";
        }
    }

    /**
     * 获取Bi埋点上传的url
     * 裁剪前:adUrl=https://m.aomygod.com/act-app-xiajimianmo.html?params=111
     * 裁剪后:act-app-xiajimianmo
     */
    public static String getBiSubUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                String subStr = "m.aomygod.com";
                int i = 0;
                if (url.contains(subStr)) {
                    i = url.indexOf(subStr) + subStr.length();
                }
                if (url.contains(".html")) {
                    int j = url.indexOf(".html");
                    return url.substring(i, j);
                } else {
                    if (url.contains("?")) {
                        int k = url.indexOf("?");
                        return url.substring(i, k);
                    } else {
                        return url.substring(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getBiSubUrlPXC(String url) {
        String result = "";
//        if (!TextUtils.isEmpty(url)) {
        if (url != null) {
            String amgUrl = "m.aomygod.com";
            String wxUrl = "mp.weixin.qq.com";
            if (url.contains(amgUrl)) {
                result = cutUrl(url, amgUrl);
            } else if (url.contains(wxUrl)) {
                result = cutUrl(url, wxUrl);
            } else {
                result = url;
            }
        }
        return result;
    }

    private static String cutUrl(String url, String matchUrl) {
        if (url == null) return "";
        int i = url.indexOf(matchUrl) + matchUrl.length() + 1;
        if (i > 0 && i < url.length()) {
            url = url.substring(i);
        }
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.contains(".html")) {
            url = url.substring(0, url.indexOf(".html"));
        }
        return url;
    }

    /**
     * 获取Bi埋点上传的url,精选话题特殊处理,要保留id,总之要过滤掉所有的".",bpm的每一个拼接的参数都不能再包含"."
     * 裁剪前:adUrl=https://m.aomygod.com/act-app-xiajimianmo?id=111
     * 裁剪后:act-app-xiajimianmo?id=111
     */
    public static String getBiSubUrl4Topic(String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                String subStr = "m.aomygod.com";
                int i = url.indexOf(subStr) + subStr.length() - 1;
                if (url.contains(".html")) {
                    int j = url.indexOf(".html");
                    return url.substring(i + 1, j);
                } else {
                    return url.substring(i + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }
}
