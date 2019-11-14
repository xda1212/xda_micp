package com.tuanmai.tools;

import android.content.Context;

import com.tuanmai.tools.Utils.PreferencesUtil;
import com.tuanmai.tools.Utils.ResUtil;
import com.tuanmai.tools.Utils.ScreenUtil;

/**
 * Created by LiuQiCong
 *
 * @date 2017-06-07 15:24
 * version 1.0
 * dsc ToolsLibraray模块库初始化
 */
public final class ToolsLibraray {

    private volatile static ToolsLibraray mInstance;
    private static Context mContext;

    public static ToolsLibraray init(Context context) {
        if (mInstance == null) {
            synchronized (ToolsLibraray.class) {
                if (mInstance == null) {
                    mInstance = new ToolsLibraray(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private ToolsLibraray(Context context){
        mContext=context;

        ResUtil.init(context);
        ScreenUtil.init(context);
        PreferencesUtil.init(context);
    }


    public static Context getContext(){
        return mContext;
    }

}
