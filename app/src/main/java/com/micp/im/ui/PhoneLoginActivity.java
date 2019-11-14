package com.micp.im.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.micp.im.AppConfig;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.bean.UserBean;
import com.micp.im.ui.customviews.ActivityTitle;
import com.micp.im.utils.LoginUtils;
import com.micp.im.utils.TDevice;
import com.micp.im.utils.TLog;
import com.micp.im.utils.UIHelper;
import com.micp.im.widget.BlackEditText;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiguang.jmlinksdk.api.JMLinkAPI;
import cn.jiguang.jmlinksdk.api.JMLinkInterface;
import cn.jiguang.jmlinksdk.api.annotation.JMLinkDefaultRouter;
import cn.jiguang.jmlinksdk.api.annotation.JMLinkRouter;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;

/**
 * 手机登陆
 */
@JMLinkRouter(keys = "invite")
@JMLinkDefaultRouter
public class PhoneLoginActivity extends ToolBarBaseActivity {

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @InjectView(R.id.et_loginphone)
    BlackEditText mEtUserPhone;

    @InjectView(R.id.et_password)
    BlackEditText mEtUserPassword;

    @InjectView(R.id.ll_login_type)
    LinearLayout mLvLoginType;

    @InjectView(R.id.other_login_wechat)
    View mLoginWechat;
    @InjectView(R.id.other_login_qq)
    View mLoginQQ;
    @InjectView(R.id.other_login_weibo)
    View mLoginWeibo;

    @InjectView(R.id.btn_dologin)
    View mLoginButton;

    private String type;
    private String[] names = {QQ.NAME, Wechat.NAME};//, SinaWeibo.NAME};
    private JSONArray name;
    private UMShareAPI mShareAPI;
    private Object lock = new Object();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }


    @Override
    public void initView() {
        mActivityTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showMobileRegLogin(PhoneLoginActivity.this);
            }
        });

        mEtUserPhone.addTextChangedListener(textWatcher);
        mEtUserPassword.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(mEtUserPhone.getText().toString()) && !TextUtils.isEmpty(mEtUserPassword.getText().toString())) {
                mLoginButton.setEnabled(true);
            } else {
                mLoginButton.setEnabled(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过intent方式获取动态参数值
        Intent intent = getIntent();
        AppContext.getInstance().setParentId(intent.getStringExtra("user_id"));

        if (AppContext.getInstance().isLogin()) {
            UIHelper.showMainActivity(this);
            finish();
        }
    }

    @Override
    public void initData() {
        mShareAPI = UMShareAPI.get(getApplicationContext());

        //每次授权都调起授权页
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        mShareAPI.setShareConfig(config);

    }


    @OnClick({R.id.btn_dologin, R.id.btn_doReg, R.id.tv_findPass, R.id.other_login_wechat, R.id.other_login_weibo,
            R.id.other_login_qq, R.id.tv_login_declaration, R.id.tv_login_clause})
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_dologin) {
            if (prepareForLogin()) {
                return;
            }
            String mUserName = mEtUserPhone.getText().toString();
            String mPassword = mEtUserPassword.getText().toString();

            showWaitDialog(R.string.loading);

            PhoneLiveApi.login(mUserName, mPassword, callback);

        } else if (v.getId() == R.id.btn_doReg) {

            UIHelper.showMobileRegLogin(this);

        } else if (v.getId() == R.id.tv_findPass) {

            UIHelper.showUserFindPass(this);
        } else if (v.getId() == R.id.other_login_wechat) {
            mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, mAauthListener);
        } else if (v.getId() == R.id.other_login_qq) {
            mShareAPI.getPlatformInfo(this, SHARE_MEDIA.QQ, mAauthListener);
        } else if (v.getId() == R.id.other_login_weibo) {
            mShareAPI.getPlatformInfo(this, SHARE_MEDIA.SINA, mAauthListener);
        } else if (v.getId() == R.id.tv_login_clause) {
            UIHelper.showWebView(this, AppConfig.CLAUSE_URL, "");
        } else if (v.getId() == R.id.tv_login_declaration) {
            UIHelper.showWebView(this, AppConfig.DECLARATION_URL, "");
        }
    }

    UMAuthListener mAauthListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            showWaitDialog("正在授权登录...", true);
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            String uid = data.get("uid");
            String userName = data.get("name");
            String icon = data.get("iconurl");

            showWaitDialog();
            PhoneLiveApi.otherLogin(type, uid, userName, icon, AppContext.getInstance().getParentId(), callback);
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            hideWaitDialog();
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            hideWaitDialog();
        }
    };

    //登录回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            hideWaitDialog();
            AppContext.showToast("网络请求出错!");
        }

        @Override
        public void onResponse(String s, int id) {
            hideWaitDialog();
            JSONArray requestRes = ApiUtils.checkIsSuccess(s);
            if (requestRes != null) {
                Gson gson = new Gson();
                try {
                    UserBean user = gson.fromJson(requestRes.getString(0), UserBean.class);

                    AppContext.getInstance().saveUserInfo(user);

                    LoginUtils.getInstance().OtherInit(PhoneLoginActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private boolean prepareForLogin() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return true;
        }

        if (mEtUserPhone.length() == 0) {
            mEtUserPhone.setError("请输入手机号码");
            mEtUserPhone.requestFocus();
            return true;
        }
        if (mEtUserPhone.length() != 11) {
            mEtUserPhone.setError("请输入11位的手机号码");
            mEtUserPhone.requestFocus();
            return true;
        }

        //HHH 2016-09-09
        if (mEtUserPassword.length() == 0) {
            mEtUserPassword.setError("请输入密码");
            mEtUserPassword.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideWaitDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
