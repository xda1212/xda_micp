package com.tuanmai.tools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.tuanmai.tools.R;
import com.tuanmai.tools.Utils.ResUtil;
import com.tuanmai.tools.widget.unscroll.UnScrollListView;

/**
 * Created by dengyi on 2017/10/26.
 */

public class BottomDialog {
    private Context mContext;
    private Dialog mDialog;

    private TextView mTitleTextView;
    private ImageView mTitleIcon;
    private UnScrollListView mListView;

    public BottomDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext, R.style.tools_CustomDialog);
        initView();
    }

    public void initDialog(@Nullable String titleString, @NonNull ListAdapter adapter, @Nullable AdapterView.OnItemClickListener itemOnClickListener) {
        if (null != titleString) {
            mTitleTextView.setText(titleString);
        } else {
            mTitleTextView.setVisibility(View.GONE);
        }
        mTitleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mListView.setAdapter(adapter);
        if (null != itemOnClickListener) {
            mListView.setOnItemClickListener(itemOnClickListener);
        }
    }

    public void initDialog(@Nullable String titleString, @DrawableRes int titleIconResId, @NonNull View.OnClickListener titleIconOnClickListener,
                           @NonNull ListAdapter adapter, @Nullable AdapterView.OnItemClickListener itemOnClickListener) {
        initDialog(titleString, adapter, itemOnClickListener);
        mTitleIcon.setImageDrawable(ResUtil.getDrawable(titleIconResId));
        mTitleIcon.setOnClickListener(titleIconOnClickListener);
    }

    private void initView() {
        mDialog.setContentView(R.layout.tools_bottom_dialog_layout);
        mTitleTextView = (TextView) mDialog.findViewById(R.id.bottom_dialog_title);
        mTitleIcon = (ImageView) mDialog.findViewById(R.id.bottom_dialog_title_icon);
        mListView = (UnScrollListView) mDialog.findViewById(R.id.bottom_dialog_listview);

        //获取当前Activity所在的窗体
        Window dialogWindow = mDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutInflater = dialogWindow.getAttributes();
        layoutInflater.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialogWindow.setAttributes(layoutInflater);
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
}
