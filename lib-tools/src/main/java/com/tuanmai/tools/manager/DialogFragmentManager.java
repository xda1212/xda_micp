package com.tuanmai.tools.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.tuanmai.tools.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Version : 3.1.1
 * Author: zzh
 * Created : 2017/12/11
 * Des :
 */
public final class DialogFragmentManager {

    private static DialogFragmentManager sDialogManger;
    private static final int SHOW_NEXT_DIALOG = 999;
    public static final String DIALOG_TAG = "BaseDialogFragment";

    private int dialog_amount = 0;//窗口总数
    private int dialog_index = 0;//现在显示的是第几个窗口
    private Activity mActivity;
    private List<BaseDialogFragment> mDialogList = new ArrayList();

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_NEXT_DIALOG:
                    if (mDialogList.size() > dialog_index && mActivity != null) {
                        show(mActivity, mDialogList.get(dialog_index));
                    }
                    break;
            }
        }
    };

    private DialogFragmentManager() {
    }

    public static DialogFragmentManager getInstance() {
        if (sDialogManger == null) {
            synchronized (DialogFragmentManager.class) {
                if (sDialogManger==null) {
                    sDialogManger = new DialogFragmentManager();
                }
            }
        }
        return sDialogManger;
    }

    public void showDialog(final Activity activity, final BaseDialogFragment fragment) {
        mActivity = activity;
        if (mDialogList.size() == 0) {
            show(activity, fragment);
        }
        mDialogList.add(fragment);
        dialog_amount++;
    }

    private void show(Activity activity, BaseDialogFragment fragment) {
        fragment.show(activity.getFragmentManager(), DIALOG_TAG);
        dialog_index++;
    }


    public void dismissDialog() {
        //最后一个窗口dismiss
        if (dialog_amount == dialog_index) {
            initData();
            mActivity = null;
        } else {
            handler.sendEmptyMessage(SHOW_NEXT_DIALOG);
        }
    }

    public void initData() {
        mDialogList.clear();
        dialog_amount = 0;
        dialog_index = 0;
    }
}
