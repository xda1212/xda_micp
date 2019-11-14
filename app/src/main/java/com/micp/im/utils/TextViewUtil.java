package com.micp.im.utils;

import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;

public class TextViewUtil {
    //计算行数
    public static int getLineCount(float textSize, int viewWidth, String textContent) {
        //用DynamicLayout，在TextView未绘制的情况下，计算textView行数
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        DynamicLayout textLayout = new DynamicLayout(textContent,
                textPaint, viewWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        return textLayout.getLineCount();
    }
}
