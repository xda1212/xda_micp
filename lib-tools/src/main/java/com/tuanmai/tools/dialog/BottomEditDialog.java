package com.tuanmai.tools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.tuanmai.tools.R;

/**
 * @author dengyi
 * @version 1.0
 * @date 2017/11/28
 * @desc
 */
public class BottomEditDialog {
    private Context mContext;
    private Dialog mDialog;

    private EditText mEditText;
    private View mSubmitButton;

    private BottomEditDialogCallback mCallback;

    public BottomEditDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext, R.style.tools_CustomDialog);
        initView();
    }

    private void initView() {
        mDialog.setContentView(R.layout.tools_bottom_edit_dialog_layout);
        mEditText = (EditText) mDialog.findViewById(R.id.bottom_edit_dialog_edit_text);
        mEditText.requestFocus();
        mSubmitButton = mDialog.findViewById(R.id.bottom_edit_dialog_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mCallback){
                    mCallback.onSubmitButtonClickListener(mEditText.getText().toString());
                }
            }
        });

        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = d.widthPixels; // 宽度设置为屏幕宽度
        dialogWindow.setAttributes(lp);
    }

    public void showDialog() {
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public interface BottomEditDialogCallback {
        void onSubmitButtonClickListener(String editTextContent);
    }

    public void setCallback(BottomEditDialogCallback mCallback) {
        this.mCallback = mCallback;
    }
}
