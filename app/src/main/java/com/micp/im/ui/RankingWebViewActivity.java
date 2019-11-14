package com.micp.im.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.micp.im.AppConfig;
import com.micp.im.R;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.ui.customviews.ActivityTitle;

import java.util.Locale;

import butterknife.InjectView;

public class RankingWebViewActivity extends ToolBarBaseActivity {


    @InjectView(R.id.rank_wv)
    WebView mWebView;

    private String mUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ranking_web_view;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

        mUid = getIntent().getStringExtra("uid");
        showData(false);

    }

    private void showData(boolean b) {

        String url = String.format(Locale.CHINA,
                AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=ranking");
        mWebView.loadUrl(url);
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    public static void startOrderWebView(Context context, String uid) {
        Intent intent = new Intent(context, RankingWebViewActivity.class);
        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }
}
