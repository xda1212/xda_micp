package com.tuanmai.tools.base;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;

import com.tuanmai.tools.manager.DialogFragmentManager;

/**
 * Version : 3.1.1
 * Author: zzh
 * Created : 2017/12/11
 * Des :
 *
 *    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);    避免低版本手机出现蓝色title背景
 */
public class BaseDialogFragment extends DialogFragment {

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            e.printStackTrace();
            //异常了清空数据，防止后续窗口无法显示
            DialogFragmentManager.getInstance().initData();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            DialogFragmentManager.getInstance().initData();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        DialogFragmentManager.getInstance().dismissDialog();
        super.onDismiss(dialog);
    }
}
