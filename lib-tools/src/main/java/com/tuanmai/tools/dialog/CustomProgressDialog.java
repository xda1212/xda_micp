package com.tuanmai.tools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuanmai.tools.R;
import com.tuanmai.tools.Utils.LogUtil;

public class CustomProgressDialog extends Dialog {

    private Context context = null;
    private ImageView loadingImageView;
    private Animation operatingAnim;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.tools_customprogressdialog);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        loadingImageView = findViewById(R.id.loadingImageView);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        operatingAnim = AnimationUtils.loadAnimation(context,
                R.anim.tools_progress_round_plan);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null && loadingImageView != null) {
            loadingImageView.startAnimation(operatingAnim);
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            if (loadingImageView != null) {
                loadingImageView.clearAnimation();
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }

    }

    public void setMessage(String strMessage) {
        TextView tvMsg = findViewById(R.id.id_tv_loadingmsg);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
    }
}
