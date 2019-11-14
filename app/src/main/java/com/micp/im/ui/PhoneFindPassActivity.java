package com.micp.im.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.util.NetUtils;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.ui.customviews.ActivityTitle;
import com.micp.im.utils.DialogHelp;
import com.micp.im.utils.SimpleUtils;
import com.micp.im.utils.TDevice;
import com.micp.im.widget.BlackEditText;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import java.lang.ref.WeakReference;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 *手机登陆 HHH 2016-09-09
 */
public class PhoneFindPassActivity extends ToolBarBaseActivity {
    @InjectView(R.id.et_loginphone)
    BlackEditText mEtUserPhone;
    @InjectView(R.id.et_logincode)
    BlackEditText mEtCode;
    @InjectView(R.id.btn_phone_login_send_code)
    TextView mBtnSendCode;

    @InjectView(R.id.et_password)
    BlackEditText mEtUserPassword;
    @InjectView(R.id.et_secondPassword)
    BlackEditText mEtSecondPassword;

    /*@InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;
    */
    @InjectView(R.id.iv_find_pass_return)
    ImageView iv_find_pass_return;


    //HHH 2016-09-09
    private String mUserName = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_pass;
    }

    @Override
    public void initView() {
        mBtnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });

        /*mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/


        iv_find_pass_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendCode() {

        mUserName = mEtUserPhone.getText().toString();
        if(!mUserName.equals("") && mUserName.length() == 11) {


            if(!NetUtils.hasNetwork(PhoneFindPassActivity.this)){
                showToast3("请检查网络设置",0);
                return;
            }
            PhoneLiveApi.getMessageCode(mUserName, "Login.getForgetCode", new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    if(res != null){
                        showToast3(getString(R.string.codehasbeensend),0);
                    }
                }
            });

            SimpleUtils.startTimer(new WeakReference<TextView>(mBtnSendCode),"发送验证码",60,1);
        }
        else{
            showToast3(getString(R.string.plase_check_you_num_is_correct),0);
        }

    }


    @Override
    public void initData() {

    }

    @OnClick(R.id.btn_doResetPassword)
    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_doResetPassword) {

            if (prepareForFindPass()) {
                return;
            }
            mUserName = mEtUserPhone.getText().toString();
            String mCode = mEtCode.getText().toString();
            String mPassword=  mEtUserPassword.getText().toString();
            String mSecondPassword= mEtSecondPassword.getText().toString();
            showWaitDialog(R.string.loading);
            PhoneLiveApi.findPass(mUserName, mPassword,mSecondPassword, mCode, callback);
        }


    }
    //注册回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e,int id) {
            AppContext.showToast("网络请求出错!");
        }

        @Override
        public void onResponse(String s,int id) {

            hideWaitDialog();

            JSONArray res = ApiUtils.checkIsSuccess(s);

            if(res != null){

                AlertDialog alertDialog = DialogHelp.getMessageDialog(PhoneFindPassActivity.this, "密码修改成功", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setTitle("提示").create();

                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }


        }
    };

    //HHH 2016-09-09
    private boolean prepareForFindPass() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return true;
        }
        if (mEtUserPhone.length() == 0) {
            mEtUserPhone.setError("请输入手机号码/用户名");
            mEtUserPhone.requestFocus();
            return true;
        }

        if (mEtCode.length() == 0) {
            mEtCode.setError("请输入验证码");
            mEtCode.requestFocus();
            return true;
        }

        if (mEtUserPassword.length() == 0) {
            mEtUserPassword.setError("请输入密码");
            mEtUserPassword.requestFocus();
            return true;
        }


        if (!mEtSecondPassword.getText().toString().equals(mEtUserPassword.getText().toString())) {
            mEtSecondPassword.setText("");
            mEtSecondPassword.setError("密码不一致，请重新输入");
            mEtSecondPassword.requestFocus();
            return true;
        }

        return false;
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
