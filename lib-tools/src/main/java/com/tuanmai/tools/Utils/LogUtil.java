package com.tuanmai.tools.Utils;


import android.util.Log;

/**
 * 日志工具类
 */
public class LogUtil {

	/**
	 * LOG标签
	 */
	private static String TAG = "amj";

	/**
	 * 是否调试模式
	 */
	private static boolean isDebug = true;

	/**
	 * 设置调试模式
	 * 
	 * @param isDebug
	 * @return
	 */
	public static void setDebug(boolean isDebug) {
		LogUtil.isDebug = isDebug;
	}

	/**
	 * 设置标签
	 * 
	 * @param tag
	 * @return
	 */
	public static void setTAG(String tag) {
		LogUtil.TAG = tag;
	}

	/**
	 * 调试内容
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg) {
		if (isDebug) {
			Log.d(tag, msg);
		}
	}

	/**
	 * 调试内容
	 * 
	 * @param msg
	 * @param msg
	 */
	public static void d(String msg) {
		if (isDebug) {
			Log.d(LogUtil.TAG, msg);
		}
	}

	/**
	 * 异常发生
	 */
	public static void e(Exception e) {
		if (isDebug) {
			if (null != e) {
				e(e.toString());
			}
		}
	}

    public static void e(String info){
        if (isDebug) {
            Log.e(TAG, info);
        }
    }

}
