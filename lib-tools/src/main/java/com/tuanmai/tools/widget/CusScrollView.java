package com.tuanmai.tools.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by LiuQiCong
 * date 2017-11-21 16:40
 * version 1.0
 * dsc 描述
 */
public class CusScrollView extends ScrollView{

    private boolean interceptAnyEvent;
    private float downY;

    public CusScrollView(Context arg0) {
        this(arg0, null);
    }

    public CusScrollView(Context arg0, AttributeSet arg1) {
        this(arg0, arg1, 0);
    }

    public CusScrollView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 处理任何事件
        if(interceptAnyEvent){
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                downY = ev.getRawY();

            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                float gapY=ev.getRawY() - downY;
                if (gapY>0 && getScrollY()==0) {
                    //向下
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }else if(gapY<0){
                    //向上
                    View childView = getChildAt(0);
                    if(childView.getMeasuredHeight()+getPaddingTop()+getPaddingBottom()
                            <= getScrollY() + getHeight()){
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                }
            }
            getParent().requestDisallowInterceptTouchEvent(true);
        }else{
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        return super.dispatchTouchEvent(ev);
    }

    public void setInterceptAnyEvent(boolean yes){
        interceptAnyEvent=yes;
    }

}
