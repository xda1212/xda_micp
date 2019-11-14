package com.micp.im.ui;

import android.view.View;

import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.ui.customviews.ActivityTitle;
import com.micp.im.utils.DialogHelp;
import com.micp.im.widget.BlackButton;
import com.micp.im.widget.BlackEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

public class RequestCashActivity extends ToolBarBaseActivity {


    @InjectView(R.id.et_cash_num)
    BlackEditText etCashNum;
    @InjectView(R.id.btn_request_cash)
    BlackButton btnRequestCash;

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_request_cash;
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

    }

    @OnClick(R.id.btn_request_cash)
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_request_cash:
                if(etCashNum.getText().toString().equals("")) {
                    showToast2("请输入提现金额");
                    return;
                }


                showWaitDialog2("正在提交信息",false);
                PhoneLiveApi.requestCash(getUserID(),getUserToken(),etCashNum.getText().toString(),
                        new StringCallback(){

                            @Override
                            public void onError(Call call, Exception e,int id) {
                                hideWaitDialog();
                                AppContext.showToastAppMsg(RequestCashActivity.this,"接口请求失败");
                            }

                            @Override
                            public void onResponse(String response,int id) {
                                hideWaitDialog();
                                JSONArray res = ApiUtils.checkIsSuccess(response);
                                if(null != res){

                                    try {
                                        DialogHelp.getMessageDialog(RequestCashActivity.this,res.getJSONObject(0).getString("msg"))
                                                .create()
                                                .show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                break;

            default:
                break;
        }


    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("requestCash");
    }
}
