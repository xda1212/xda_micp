package com.micp.im.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.micp.im.R;
import com.micp.im.base.AbsFragment;
import com.tuanmai.tools.Utils.ScreenUtil;
import com.tuanmai.tools.Utils.fresco.FrescoUtils;


public final class ImageFragment extends AbsFragment {

    SimpleDraweeView imageView;
    private String url;

    public static ImageFragment newInstance(String url) {
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }


    public void initView() {
        imageView = (SimpleDraweeView) getView().findViewById(R.id.image_ad_banner);
        update(url);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragement_image, container, false);
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    public void update(String filePath) {
        FrescoUtils.setControllerListener(imageView, Uri.parse("file://" + filePath), ScreenUtil.getScreenWidth());
    }
}
