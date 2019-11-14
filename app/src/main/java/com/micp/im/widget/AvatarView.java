package com.micp.im.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.micp.im.AppConfig;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.utils.SimpleUtils;
import com.micp.im.utils.StringUtils;

public class AvatarView extends CircleImageView {
    public static final String AVATAR_SIZE_REG = "_[0-9]{1,3}";
    public static final String MIDDLE_SIZE = "_100";
    public static final String LARGE_SIZE = "_200";

    private int id;
    private String name;

    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AvatarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {

//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(name)) {
//                    //UIHelper.showUserCenter(getContext(), id, name);
//                }
//            }
//        });
    }

    public void setUserInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setAvatarUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            setImageResource(R.drawable.null_blacklist);
            return;
        }
        if (!url.startsWith("http")) {
            url = AppConfig.MAIN_URL + url;
        }

        SimpleUtils.loadImageForView(AppContext.getInstance(),this,url,R.drawable.icon_load_fail_2);

    }

    public static String getSmallAvatar(String source) {
        return source;
    }

    public static String getMiddleAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, MIDDLE_SIZE);
    }

    public static String getLargeAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, LARGE_SIZE);
    }
}
