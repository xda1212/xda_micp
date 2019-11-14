package com.tuanmai.tools.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;

import com.tuanmai.tools.widget.CustomImageSpan;

/**
 * 功能：文本工具
 */
public class TextUtil {

    /**
     * 功能：TextView显示颜色HTML
     *
     * @param text
     * @param color "#00b4ff"
     * @return
     */
    public static String toColor(String text, String color) {
        return "<font color=\"" + color + "\">" + text + "</font>";
    }

    /**
     * 功能：TextView显示颜色斜体HTML
     *
     * @param text
     * @param color "#00b4ff"
     * @return
     */
    public static String toColorItalic(String text, String color) {
        return "<font color=\"" + color + "\" style=\"font-weight:bold;font-style:italic;\">" + text + "</font>";
    }

    /**
     * @param right
     * @param bottom
     * @param content 内容
     * @param resId   替换图片
     * @return
     */
    public static SpannableString addTimeLimit(final int right,
                                               final int bottom, String content, final int resId) {
        SpannableString spannableString = new SpannableString("1 " + content);
        spannableString.setSpan(new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BOTTOM) {
            @Override
            public Drawable getDrawable() {
                Drawable drawable = ResUtil.getDrawable(resId);
                if (drawable != null) {
                    drawable.setBounds(0, 0,
                            ScreenUtil.dp2px(right), ScreenUtil.dp2px(bottom));
                }
                return drawable;
            }
        }, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 修改字符串某部分的颜色
     * @param context
     * @param textString
     * @param colorResourceId 颜色id
     * @param start 开始index
     * @param end 结算index
     * @return
     */
    public static SpannableString changeTextColor(Context context, String textString, @ColorRes int colorResourceId, int start, int end){
        SpannableString spannableString = new SpannableString(textString);
        int colorId = context.getResources().getColor(colorResourceId);
        spannableString.setSpan(new ForegroundColorSpan(colorId), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static SpannableString setImageSpan(Context context,String key, String value,int resId) {
        SpannableString spannableString = new SpannableString(value);

        int index = value.indexOf(key);
        if (index >= 0) {
            while (index < value.length() && index >= 0) {
                CustomImageSpan imageSpan = new CustomImageSpan(context.getApplicationContext(), resId);
                spannableString.setSpan(imageSpan, index, index + key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                index = value.indexOf(key, index + key.length());
            }
        }
        return spannableString;
    }
}
