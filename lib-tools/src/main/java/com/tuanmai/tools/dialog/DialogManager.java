package com.tuanmai.tools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuanmai.tools.R;
import com.tuanmai.tools.Utils.LogUtil;
import com.tuanmai.tools.Utils.ScreenUtil;

/**
 * 弹窗
 */
public final class DialogManager {

    private volatile static DialogManager mInstance = null;
    private Dialog dialog = null;

    private DialogManager() {
    }

    public static DialogManager getInstance() {
        if (mInstance == null) {
            synchronized (DialogManager.class) {
                if (mInstance == null) {
                    mInstance = new DialogManager();
                }
            }
        }
        return mInstance;
    }


    //====================================单按钮======================================

    /**
     * @param context
     * @param msg        对话框信息
     * @param okListener 确认按钮事件
     */
    public void showDialog(Context context, String msg, OnClickListener okListener) {
        showDialog(context, msg, null, okListener);
    }

    /**
     * @param context
     * @param msg        对话框信息
     * @param btnText    确认按钮文字
     * @param okListener 确认按钮事件
     */
    public Dialog showDialog(Context context, String msg, String btnText,
                             final OnClickListener okListener) {
        try {
            hideDialog();
            if (null == context) {
                return null;
            }
            dialog = new Dialog(context, R.style.tools_CustomDialog);
            dialog.setCancelable(true);

            View view = LayoutInflater.from(context).inflate(R.layout.tools_dialog_layout, null);
            dialog.setContentView(view);

            Button okBtn = view.findViewById(R.id.dialog_ok_btn);
            okBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null != okListener) {
                        okListener.onClick(v);
                    }
                    hideDialog();
                }
            });
            if (!TextUtils.isEmpty(btnText)) {
                okBtn.setText(btnText);
            }
            ((TextView) view.findViewById(R.id.dialog_content)).setText(msg);
            dialog.show();
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return dialog;
    }


    /**
     * 自定义View
     *
     * @param context
     * @param view       对话框内容
     * @param btnText    确认按钮文字
     * @param okListener 确认按钮事件
     */
    public Dialog showDialog(Context context, View view, String btnText,
                             final OnClickListener okListener) {
        hideDialog();
        if (null == context) {
            return null;
        }
        dialog = new Dialog(context, R.style.tools_CustomDialog);
        dialog.setCancelable(true);
        View rootView = LayoutInflater.from(context).inflate(R.layout.tools_dialog_layout, null);
        dialog.setContentView(rootView);

        Button okBtn = rootView.findViewById(R.id.dialog_ok_btn);
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != okListener) {
                    okListener.onClick(v);
                }
                hideDialog();
            }
        });
        if (!TextUtils.isEmpty(btnText)) {
            okBtn.setText(btnText);
        }
        LinearLayout content = rootView.findViewById(R.id.content_layout);
        content.removeAllViews();
        content.addView(view);
        dialog.show();
        return dialog;
    }


    //=====================================双按钮========================================

    /**
     * @param context
     * @param msg        对话框信息
     * @param sText      确认按钮文字
     * @param sBold      确认按钮是否加粗
     * @param cText      取消按钮文字
     * @param cBold      取消按钮是否加粗
     * @param okListener 确认按钮事件
     */
    public Dialog showDialog(Context context, String msg,
                             String sText, boolean sBold,
                             String cText, boolean cBold,
                             final OnClickListener okListener) {
        return showDialog(context, msg, sText, sBold, cText, cBold, okListener, null);
    }

    /**
     * @param context
     * @param msg        对话框信息
     * @param sText      确认按钮文字
     * @param cText      取消按钮文字
     * @param okListener 确认按钮事件
     */
    public Dialog showDialog(Context context, String msg,
                             String sText,
                             String cText,
                             final OnClickListener okListener) {
        return showDialog(context, msg, sText, true, cText, false, okListener, null);
    }

    /**
     * 带有标题，且中间内容居中显示Dialog
     *
     * @param context
     * @param title      标题
     * @param msg        内容
     * @param rightText  确定文字/右边文字
     * @param leftText   取消文字/左边文字
     * @param okListener 确定监听
     * @return
     */
    public Dialog showDialog(Context context, String title, String msg, String rightText, String leftText, final OnClickListener okListener) {
        hideDialog();
        if (context == null) {
            return null;
        }
        dialog = new Dialog(context, R.style.tools_CustomDialog);
        dialog.setCancelable(true);

        View rootView = LayoutInflater.from(context).inflate(R.layout.tools_dialog_title_layout, null);
        dialog.setContentView(rootView);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.tools_AnimDialog);
        }
        //设置宽度
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ScreenUtil.getScreenWidth() / 10 * 7;
        window.setAttributes(params);

        //确认
        Button okBtn = rootView.findViewById(R.id.dialog_ok_btn);
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (okListener != null) {
                    okListener.onClick(v);
                }
                hideDialog();
            }
        });
        if (!TextUtils.isEmpty(rightText)) {
            okBtn.setText(rightText);
        }
        //取消
        Button cancelBtn = rootView.findViewById(R.id.dialog_canle_btn);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
            }
        });
        if (!TextUtils.isEmpty(leftText)) {
            cancelBtn.setText(leftText);
        }
        if (leftText == null) {
            cancelBtn.setVisibility(View.GONE);
            rootView.findViewById(R.id.btn_line).setVisibility(View.GONE);
        }

        ((TextView) rootView.findViewById(R.id.dialog_content)).setText(msg);
        ((TextView) rootView.findViewById(R.id.dialog_title)).setText(title);
        try {
            dialog.show();
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return dialog;
    }

    /**
     * @param context
     * @param msg            对话框信息
     * @param sText          确认按钮文字
     * @param cText          取消按钮文字
     * @param okListener     确认按钮事件
     * @param cancelListener 取消按钮事件
     */
    public Dialog showDialog(Context context, String msg,
                             String sText, boolean sBold,
                             String cText, boolean cBold,
                             final OnClickListener okListener,
                             final OnClickListener cancelListener) {
        hideDialog();
        if (null == context) {
            return null;
        }
        dialog = new Dialog(context, R.style.tools_CustomDialog);
        dialog.setCancelable(true);

        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.tools_dialog_canl_layout, null);
        dialog.setContentView(rootView);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.tools_AnimDialog);
        //设置宽度
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ScreenUtil.getScreenWidth() / 10 * 7;
        window.setAttributes(params);

        //确认
        Button okBtn = rootView.findViewById(R.id.dialog_ok_btn);
        okBtn.getPaint().setFakeBoldText(sBold);
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (okListener != null) {
                    okListener.onClick(v);
                }
                hideDialog();
            }
        });
        if (!TextUtils.isEmpty(sText)) {
            okBtn.setText(sText);
        }
        //取消
        Button cancelBtn = rootView.findViewById(R.id.dialog_canle_btn);
        cancelBtn.getPaint().setFakeBoldText(cBold);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelListener != null) {
                    cancelListener.onClick(view);
                }
                hideDialog();
            }
        });
        if (!TextUtils.isEmpty(cText)) {
            cancelBtn.setText(cText);
        }

        ((TextView) rootView.findViewById(R.id.dialog_content)).setText(msg);
        try {
            dialog.show();
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return dialog;
    }

    //=====================================================================================

    /**
     * 隐藏弹窗
     */
    public void hideDialog() {
        try {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception e) {
        }
    }


}
