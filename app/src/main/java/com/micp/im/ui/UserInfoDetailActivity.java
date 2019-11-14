package com.micp.im.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.micp.im.bean.UserBean;
import com.micp.im.ui.customviews.ActivityTitle;
import com.micp.im.utils.LiveUtils;
import com.micp.im.utils.UIHelper;
import com.micp.im.widget.AvatarView;
import com.google.gson.Gson;

import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.em.ChangInfo;
import com.micp.im.widget.BlackTextView;
import com.tuanmai.tools.Utils.ScreenUtil;
import com.ywp.addresspickerlib.AddressPickerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 用户信息详情页面
 */
public class UserInfoDetailActivity extends ToolBarBaseActivity {

    @InjectView(R.id.et_info_birthday)
    TextView etInfoBirthday;
    private UserBean mUser;
    @InjectView(R.id.rl_userHead)
    RelativeLayout mRlUserHead;
    @InjectView(R.id.rl_userNick)
    RelativeLayout mRlUserNick;
    @InjectView(R.id.rl_userSign)
    RelativeLayout mRlUserSign;
    @InjectView(R.id.rl_userSex)
    RelativeLayout mRlUserSex;
    @InjectView(R.id.tv_userNick)
    BlackTextView mUserNick;
    @InjectView(R.id.tv_userSign)
    BlackTextView mUserSign;
    @InjectView(R.id.av_userHead)
    AvatarView mUserHead;
    @InjectView(R.id.iv_info_sex)
    ImageView mUserSex;
    @InjectView(R.id.myinfo_detail_constellation_content)
    BlackTextView mConstellation;
    @InjectView(R.id.myinfo_detail_city_content)
    BlackTextView mCity;
    @InjectView(R.id.myinfo_detail_city_layout)
    View mCityLayout;

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @InjectView(R.id.myinfo_detail_header_tip)
    View mHeaderTips;
    @InjectView(R.id.myinfo_detail_nickname_tip)
    View mNicknameTips;
    @InjectView(R.id.myinfo_detail_sex_tip)
    View mSexTips;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_myinfo_detail;
    }

    @Override
    public void initView() {
        mRlUserNick.setOnClickListener(this);
        mRlUserSign.setOnClickListener(this);
        mRlUserHead.setOnClickListener(this);
        mRlUserSex.setOnClickListener(this);
        mCityLayout.setOnClickListener(this);
        final Calendar c = Calendar.getInstance();
        etInfoBirthday.setOnClickListener(new View.OnClickListener() { //生日修改
            @Override
            public void onClick(View v) {
                showSelectBirthday(c);
            }
        });

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (MainActivity.isNeedShowUserInfoDetail(mUser)) {
            showToast2("请填写必填项目");
        } else {
            finish();
        }
    }

    //生日选择
    private void showSelectBirthday(final Calendar c) {
        DatePickerDialog dialog = new DatePickerDialog(UserInfoDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                if (c.getTime().getTime() > new Date().getTime()) {
                    showToast2("请选择正确的日期");
                    return;
                }
                final String birthday = DateFormat.format("yyy-MM-dd", c).toString();
                requestSaveBirthday(birthday);

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        try {
            dialog.getDatePicker().setMinDate(new SimpleDateFormat("yyyy-MM-dd").parse("1950-01-01").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());

        dialog.show();
    }

    //保存生日
    private void requestSaveBirthday(final String birthday) {

        PhoneLiveApi.saveInfo(LiveUtils.getFiledJson("birthday", birthday),
                AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(),
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showToast2(getString(R.string.editfail));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        if (null != res) {
                            AppContext.showToastAppMsg(UserInfoDetailActivity.this, getString(R.string.editsuccess));
                            mUser = AppContext.getInstance().getLoginUser();
                            mUser.birthday = birthday;
                            AppContext.getInstance().updateUserInfo(mUser);
                            etInfoBirthday.setText(birthday);

                            sendRequiredData();
                        }
                    }
                });
    }

    @Override
    public void initData() {
        setActionBarTitle(R.string.editInfo);
        sendRequiredData();
    }

    private void sendRequiredData() {
        PhoneLiveApi.getMyUserInfo(getUserID(), getUserToken(), callback);
    }

    @Override
    public void onClick(View v) {
        if (mUser != null) {
            switch (v.getId()) {
                case R.id.rl_userNick:
                    UIHelper.showEditInfoActivity(
                            this, "修改昵称",
                            getString(R.string.editnickpromp),
                            mUser.user_nicename,
                            ChangInfo.CHANG_NICK);
                    break;
                case R.id.rl_userSign:
                    UIHelper.showEditInfoActivity(
                            this, "修改签名",
                            getString(R.string.editsignpromp),
                            mUser.signature,
                            ChangInfo.CHANG_SIGN);
                    break;
                case R.id.rl_userHead:
                    UIHelper.showSelectAvatar(this, mUser.avatar);
                    break;
                case R.id.rl_userSex:
                    if (mUser.sex.equals("0")) {
                        UIHelper.showChangeSex(this);
                    } else {
                        Toast.makeText(this, "性别不能修改", Toast.LENGTH_SHORT);
                    }
                    break;
                case R.id.myinfo_detail_city_layout:
                    showCityChoiceDialog();
                    break;
            }
        }
    }

    @Override
    protected void onRestart() {
        sendRequiredData();
        super.onRestart();
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String s, int id) {
            JSONArray res = ApiUtils.checkIsSuccess(s);
            if (res != null) {
                try {
                    mUser = new Gson().fromJson(res.getString(0), UserBean.class);
                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser != null) {
            fillUI();
        }
    }


    private void fillUI() {
        mUserHead.setAvatarUrl(mUser.avatar);
//        if (mUser.avatar.contains("girl.png") ||
//                mUser.avatar.contains("boy.png") ||
//                mUser.avatar.contains("default.jpg")) {
//            mHeaderTips.setVisibility(View.VISIBLE);
//        } else {
//            mHeaderTips.setVisibility(View.GONE);
//        }

        mUserNick.setText(mUser.user_nicename);
        if (mUser.user_nicename.startsWith("手机用户")) {
            mNicknameTips.setVisibility(View.VISIBLE);
        } else {
            mNicknameTips.setVisibility(View.GONE);
        }

        mUserSign.setText(mUser.signature);
        etInfoBirthday.setText(mUser.birthday);
        if (null != mUser.sign) {
            mConstellation.setText(mUser.sign);
        }

        int sex = Integer.valueOf(mUser.sex);
        if (sex == 1 || sex == 2) {
            mUserSex.setImageResource(LiveUtils.getSexRes(mUser.sex));
            mSexTips.setVisibility(View.GONE);
        } else {
            mSexTips.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getMyUserInfo");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    private void showCityChoiceDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog_normal);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度
        lp.height = ScreenUtil.dp2px(250); // 高度
        dialogWindow.setAttributes(lp);

        View dialogView = mInflater.inflate(R.layout.dialog_city_choice, null);
        AddressPickerView addressPickerView = dialogView.findViewById(R.id.city_choice_address_picker);
        addressPickerView.setOnAddressPickerSure(new AddressPickerView.OnAddressPickerSureListener() {
            @Override
            public void onSureClick(String address, String provinceCode, String cityCode, String districtCode) {
                if (!TextUtils.isEmpty(address)) {
                    final String[] temp = address.split(" ");
                    if (temp.length >= 2 && !TextUtils.isEmpty(temp[1])) {
                        String city = temp[1];
                        if (city.equals("市辖区") || city.equals("县")) {
                            city = temp[0];
                        }
                        mCity.setText(city);
                        final String finalCity = city;
                        PhoneLiveApi.saveInfo(LiveUtils.getFiledJson(ChangInfo.CHANG_CITY.getAction(), city),
                                getUserID(),
                                getUserToken(),
                                new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int i) {
                                        showToast2(getString(R.string.editfail));
                                    }

                                    @Override
                                    public void onResponse(String s, int i) {
                                        JSONArray res = ApiUtils.checkIsSuccess(s);
                                        if (null != res) {
                                            showToast3(getString(R.string.editsuccess), 0);
                                            mUser = AppContext.getInstance().getLoginUser();
                                            mUser.city = finalCity;
                                            AppContext.getInstance().updateUserInfo(mUser);
                                        }
                                    }
                                });
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }
}
