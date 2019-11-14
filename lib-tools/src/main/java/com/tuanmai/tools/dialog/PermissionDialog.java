package com.tuanmai.tools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tuanmai.tools.R;
import com.tuanmai.tools.Utils.PermissionUtil;
import com.tuanmai.tools.Utils.ScreenUtil;
import com.tuanmai.tools.Utils.anim.AnimUtils;

/**
 * Created by LiuQiCong
 *
 * @date 2017-02-13 14:46
 * version 1.0
 * dsc 权限设置弹窗
 */
public final class PermissionDialog extends Dialog {

    private Context mContext;

    public PermissionDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;

        initDialog();
        initView(null, null);
    }

    public PermissionDialog(Context context, int themeResId, View.OnClickListener okClickListener, View.OnClickListener cancleClickListener) {
        super(context, themeResId);
        mContext = context;

        initView(okClickListener, cancleClickListener);
        initDialog();
    }

    private void initDialog(){
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ScreenUtil.getScreenWidth() * 4 / 5; // 设置宽度
        window.setAttributes(lp);
    }

    private void initView(final View.OnClickListener okClickListener, final View.OnClickListener cancleClickListener){
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_permission, null);
        dialogView.findViewById(R.id.dialog_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AnimUtils.clickAnim(v, new AnimUtils.AnimUtilsListner() {
                    @Override
                    public void animEnd(View view) {
                        dismiss();
                        if (null != cancleClickListener) {
                            cancleClickListener.onClick(view);
                        }
                    }
                });
            }
        });
        dialogView.findViewById(R.id.dialog_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimUtils.clickAnim(v, new AnimUtils.AnimUtilsListner() {
                    @Override
                    public void animEnd(View view) {
                        PermissionUtil.autoGotoPermission(mContext);
                        dismiss();
                        if (null != okClickListener) {
                            okClickListener.onClick(view);
                        }
                    }
                });
            }
        });
        setContentView(dialogView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
