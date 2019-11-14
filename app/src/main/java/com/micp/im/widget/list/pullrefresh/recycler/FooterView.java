package com.micp.im.widget.list.pullrefresh.recycler;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micp.im.R;


/**
 * Created by LiuQiCong
 *
 * @date 2017-05-11 15:42
 * version 1.0
 * dsc 描述
 */
public class FooterView extends FrameLayout {

    private View foot;

    private View topDivider;
    private ImageView progFooter;
    private TextView tvFooter;
    private RelativeLayout btFooter;

    public FooterView(Context context) {
        super(context);
        init(context, null);
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FooterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        foot = LayoutInflater.from(context).inflate(R.layout.tools_footer_autoload, null);
        addView(foot);
        topDivider=foot.findViewById(R.id.list_getmore_divider);
        btFooter = foot.findViewById(R.id.list_getmore_foot);
        progFooter = foot.findViewById(R.id.list_getmore_progress);
        tvFooter = foot.findViewById(R.id.tv_list_getmore_info);
        setBackgroundColor(Color.WHITE);
    }


    //=================================================================================
    public void showWait() {
        //加载更多数据时的动画
        foot.setVisibility(View.VISIBLE);
        topDivider.setVisibility(View.VISIBLE);
        progFooter.setImageResource(R.drawable.tools_pull_up_animation);
        ((AnimationDrawable) progFooter.getDrawable()).start();
        tvFooter.setText("");
    }

    public void showFinish() {
        foot.setVisibility(View.VISIBLE);
        topDivider.setVisibility(View.VISIBLE);
        if (progFooter.getDrawable() != null
                && progFooter.getDrawable() instanceof AnimationDrawable){
            ((AnimationDrawable) progFooter.getDrawable()).stop();
        }
        progFooter.setImageResource(0);
        tvFooter.setText("亲，已经看到最后了");
    }

    public void showGetMoreFail() {
        foot.setVisibility(View.VISIBLE);
        topDivider.setVisibility(View.VISIBLE);
        if (progFooter.getDrawable() != null
                && progFooter.getDrawable() instanceof AnimationDrawable){
            ((AnimationDrawable) progFooter.getDrawable()).stop();
        }
        progFooter.setImageResource(0);
        tvFooter.setText("出错了？点我试试");
    }

    public void showGetMore() {
        foot.setVisibility(View.VISIBLE);
        topDivider.setVisibility(View.VISIBLE);
        if (progFooter.getDrawable() != null
                && progFooter.getDrawable() instanceof AnimationDrawable){
            ((AnimationDrawable) progFooter.getDrawable()).stop();
        }
        progFooter.setImageResource(0);
        tvFooter.setText("");
    }

    public void hideFooter() {
        foot.setVisibility(View.INVISIBLE);
    }

}
