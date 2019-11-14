package com.micp.im.ui;

import android.view.View;

import com.micp.im.AppConfig;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.ui.customviews.ActivityTitle;
import com.micp.im.ui.customviews.LineControllerView;
import com.micp.im.utils.LoginUtils;
import com.micp.im.utils.TDevice;
import com.micp.im.utils.UIHelper;
import com.micp.im.utils.UpdateManager;

import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends ToolBarBaseActivity {


    @InjectView(R.id.ll_check_update)
    LineControllerView mTvVersion;

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

        mTvVersion.setContent(String.format(Locale.CHINA, "(当前版本%s)", TDevice.getVersionName()));
    }

    @OnClick({R.id.ll_login_out, R.id.ll_room_setting, R.id.ll_clearCache, R.id.ll_push_manage, R.id.ll_about, R.id.ll_feedback, R.id.ll_blank_list, R.id.rl_change_pass, R.id.ll_check_update})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_login_out: //退出登录
                LoginUtils.outLogin(this);
                //退出登录清除直播封面、标题、M币缓存
                ReadyStartLiveActivity.protraitPath = null ;
                ReadyStartLiveActivity.stringTitle = null;
                ReadyStartLiveActivity.type_val = "";
                finish();
                break;
            //房间设置
            case R.id.ll_room_setting:

                break;
            case R.id.ll_clearCache:
                clearCache();
                break;
            case R.id.ll_push_manage:
                UIHelper.showPushManage(this);
                break;
            case R.id.ll_about:
                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=lists", "服务条款");
                break;
            //用户反馈
            case R.id.ll_feedback:
                String model = android.os.Build.MODEL;
                String release = android.os.Build.VERSION.RELEASE;
                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=newslist&uid="
                        + getUserID() + "&version=" + release + "&model=" + model, "");
                break;
            case R.id.ll_blank_list:
                UIHelper.showBlackList(SettingActivity.this);
                break;
            case R.id.rl_change_pass:
                UIHelper.showPhoneChangePassActivity(SettingActivity.this);
                break;
            case R.id.ll_check_update:
                checkNewVersion();
                break;
        }
    }

    private void checkNewVersion() {
        UpdateManager manager = new UpdateManager(this, true);
        manager.checkUpdate();


    }

    private void clearCache() {
        AppContext.getInstance().clearAppCache();
        AppContext.showToastAppMsg(this, "缓存清理成功");
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
