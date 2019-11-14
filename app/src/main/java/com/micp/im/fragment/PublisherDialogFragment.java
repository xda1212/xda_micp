package com.micp.im.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.micp.im.R;
import com.micp.im.ui.PublishCommunity;
import com.micp.im.ui.TCVideoRecordActivity;
import com.micp.im.utils.UIHelper;


/**
 * Created by carolsuo on 2017/3/7.
 * 短视频或者直播选择界面
 */

public class PublisherDialogFragment extends DialogFragment {

    private View mTVLive;
    private View mTVVideo;
    private View mTVCommunity;
    private ImageView mIVClose;

    public static final int REQUEST_CODE_LIVE = 5;
    public static final int REQUEST_CODE_VIDEO = 6;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_bottom_full);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_publisher);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        mTVLive = dialog.findViewById(R.id.tv_live);
        mTVVideo = dialog.findViewById(R.id.tv_video);
        mTVCommunity = dialog.findViewById(R.id.tv_community);

        mIVClose = (ImageView) dialog.findViewById(R.id.publisher_close);

        mTVLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startLive();
            }
        });
        mTVVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startVideo();
            }
        });
        mTVCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startCommunity();
            }
        });


        mIVClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);

        return dialog;
    }

    //开始直播初始化
    private void startLive() {
        if (Build.VERSION.SDK_INT >= 23) {
            //摄像头权限检测
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LIVE);

            } else {
                requestStartLive(getContext());
            }
        } else {
            requestStartLive(getContext());
        }
    }

    private void startVideo() {
        if (Build.VERSION.SDK_INT >= 23) {
            //摄像头权限检测
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_VIDEO);

            } else {
                requestStartVideo(getContext());
            }
        } else {
            requestStartVideo(getContext());
        }
    }

    private void startCommunity() {
        Intent intent = new Intent(getContext(), PublishCommunity.class);
        getContext().startActivity(intent);
    }

    //请求服务端开始直播
    public void requestStartLive(Context context) {
        UIHelper.showStartLiveActivity(context);
    }

    public void requestStartVideo(Context context) {
        context.startActivity(new Intent(context, TCVideoRecordActivity.class));
    }
}
