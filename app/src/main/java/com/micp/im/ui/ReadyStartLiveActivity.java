package com.micp.im.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.micp.im.AppConfig;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.bean.UserBean;
import com.micp.im.interf.DialogInterface;
import com.micp.im.ui.dialog.LiveCommon;
import com.micp.im.utils.DialogHelp;
import com.micp.im.utils.FileUtil;
import com.micp.im.utils.ImageUtils;
import com.micp.im.utils.InputMethodUtils;
import com.micp.im.utils.ShareUtils;
import com.micp.im.utils.StringUtils;
import com.micp.im.utils.TDevice;
import com.micp.im.widget.BlackEditText;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.InjectView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;

public class ReadyStartLiveActivity extends ToolBarBaseActivity {
    //填写直播标题
    @InjectView(R.id.et_start_live_title)
    BlackEditText mStartLiveTitle;

    //开始直播btn
    @InjectView(R.id.btn_start_live)
    TextView mStartLive;

    @InjectView(R.id.cb_set_pass)
    CheckBox mCbSetPass;

    @InjectView(R.id.cb_set_charge)
    CheckBox mCbCharge;

    @InjectView(R.id.iv_live_select_pic)
    ImageView mIvLivePic;

    @InjectView(R.id.ll_charge)
    LinearLayout mChargeLayout;

    @InjectView(R.id.ll_password)
    LinearLayout mPassWordLayout;

    @InjectView(R.id.cb_set_charge_h)
    CheckBox mCbChargeH;

    @InjectView(R.id.tv_live_roomtype)
    EditText mTvRoomType;

    @InjectView(R.id.iv_bg)
    ImageView mIvBg;

//    @InjectView(R.id.ll_live_share)
//    LinearLayout mShareType;

    @InjectView(R.id.ready_start_live_normal_type)
    CheckBox mNormalTypeRoom;

    @InjectView(R.id.ready_start_live_private_type)
    CheckBox mPrivateTypeRoom;

    @InjectView(R.id.ready_start_live_cost_layout)
    View mCostLayout;

    //分享模式 7为不分享任何平台
    private int shareType = -2;

    private UserBean mUser;

    private boolean isFrontCameraMirro = false;

    private boolean isClickStartLive = false;
    private String type = "0";
    public static String type_val = "";


    private final static int CROP = 400;

    private final static String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/micp/Portrait/";
    private Uri origUri;
    private Uri cropUri;
    private String theLarge;
    private File protraitFile;


    public static String protraitPath;
    public static String stringTitle;
//    public static String strLiveMb;

    private int last_item;
    private TextView oldView;
    private SharedPreferences sharedPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ready_start_live;
    }

    @Override
    public void initView() {

//        写入数据：
        //步骤1：创建一个SharedPreferences对象
        SharedPreferences sharedPreferences= getSharedPreferences("data",Context.MODE_PRIVATE);
//        //步骤2： 实例化SharedPreferences.Editor对象
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        //步骤3：将获取过来的值放入文件
//        editor.putString("protraitPath",protraitPath);
//        editor.putString("stringTitle", stringTitle);
//        editor.putString("type_val",type_val);
//        //步骤4：提交
//        editor.commit();

//        protraitPath = sharedPreferences.getString("protraitPath","");
//        stringTitle = sharedPreferences.getString("stringTitle","");
//        type_val = sharedPreferences.getString("type_val","");

        if (protraitPath != null) {
            mIvLivePic.setImageBitmap(BitmapFactory.decodeFile(protraitPath));
            protraitFile = new File(protraitPath);
        }

        if (stringTitle!=null){
            mStartLiveTitle.setText(stringTitle);
        }


        if (type_val!=null){
            mTvRoomType.setText(type_val);
        }

//        findViewById(R.id.iv_live_share_weibo).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLiveShare(v, 0);
//                shareType = 0 == shareType ? 7 : 0;
//            }
//        });
//        findViewById(R.id.iv_live_share_timeline).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLiveShare(v, 2);
//                shareType = 2 == shareType ? 7 : 2;
//            }
//        });
//        findViewById(R.id.iv_live_share_wechat).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLiveShare(v, 1);
//                shareType = 1 == shareType ? 7 : 1;
//            }
//        });
//
//        findViewById(R.id.iv_live_share_qq).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLiveShare(v, 3);
//                shareType = 3 == shareType ? 7 : 3;
//            }
//        });
//        findViewById(R.id.iv_live_share_qqzone).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLiveShare(v, 4);
//                shareType = 4 == shareType ? 7 : 4;
//            }
//        });
//        findViewById(R.id.iv_live_share_facebook).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLiveShare(v, 5);
//                shareType = 5 == shareType ? 7 : 5;
//            }
//        });
//        findViewById(R.id.iv_live_share_twitter).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLiveShare(v, 6);
//                shareType = 6 == shareType ? 7 : 6;
//            }
//        });
//        mTvRoomType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initRoomTypeDialog();
//            }
//        });

        //设置直播封面
        mIvLivePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelp.getSelectDialog(ReadyStartLiveActivity.this, new String[]{"摄像头", "相册"}
                        , new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                if (android.os.Build.VERSION.SDK_INT >= 23) {
                                    //摄像头权限检测
                                    if (ContextCompat.checkSelfPermission(ReadyStartLiveActivity.this, Manifest.permission.CAMERA)
                                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ReadyStartLiveActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        //进行权限请求
                                        ActivityCompat.requestPermissions(ReadyStartLiveActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                                5);
                                        return;
                                    } else {
                                        if (i == 0) {
                                            startTakePhoto();
                                        } else {
                                            startImagePick();
                                        }
                                    }
                                } else {
                                    if (i == 0) {
                                        startTakePhoto();
                                    } else {
                                        startImagePick();
                                    }
                                }
                            }
                        }).create().show();
            }
        });
//        if (AppConfig.SHARE_TYPE == null) {
//            mShareType.setVisibility(View.GONE);
//        } else {
//            mShareType.setVisibility(View.VISIBLE);
//            for (int i = 0; i < AppConfig.SHARE_TYPE.length(); i++) {
//                final ImageView im = new ImageView(ReadyStartLiveActivity.this);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) TDevice.dpToPixel(40), (int) TDevice.dpToPixel(60));
//                if (i > 0)
//                    lp.setMargins((int) TDevice.dpToPixel(10), 0, 0, 0);
//                im.setLayoutParams(lp);
//                try {
//                    //TODO
//                    im.setImageResource(ReadyStartLiveActivity.this.getResources().getIdentifier(AppConfig.SHARE_TYPE.getString(i) + "_share_s", "drawable", this.getPackageName()));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                mShareType.addView(im);
//                final int finalI = i;
//                im.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (shareType == finalI) {
//                            try {
//                                //TODO
//                                im.setImageResource(ReadyStartLiveActivity.this.getResources().getIdentifier(AppConfig.SHARE_TYPE.getString(finalI) + "_share_s", "drawable", ReadyStartLiveActivity.this.getPackageName()));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            try {
//                                //TODO
//                                im.setImageResource(ReadyStartLiveActivity.this.getResources().getIdentifier(AppConfig.SHARE_TYPE.getString(finalI) + "_share_s1", "drawable", ReadyStartLiveActivity.this.getPackageName()));
//                                for (int j = 0; j < AppConfig.SHARE_TYPE.length(); j++) {
//                                    ImageView imJ = (ImageView) mShareType.getChildAt(j);
//                                    if (finalI != j) {
//                                        imJ.setImageResource(ReadyStartLiveActivity.this.getResources().getIdentifier(AppConfig.SHARE_TYPE.getString(j) + "_share_s", "drawable", ReadyStartLiveActivity.this.getPackageName()));
//                                    }
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (shareType == finalI) {
//                            shareType = -2;
//                        } else {
//                            shareType = finalI;
//                        }
//                    }
//                });
//            }
//        }
    }

    public void initRoomTypeDialog() {
        final Dialog dialog = new Dialog(ReadyStartLiveActivity.this, R.style.dialog_no_background);
        dialog.setContentView(R.layout.dialog_live_select_type);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.width = (int) getResources().getDisplayMetrics().widthPixels - (int) TDevice.dpToPixel(15); // 宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);
        final JSONArray res = AppConfig.LIVE_TYPE;

        ListView mLvSelectType = (ListView) dialog.findViewById(R.id.ll_live_sharetype);
        if (res == null) return;
        mLvSelectType.setAdapter(new ListSelectTypeAdapter());
        mLvSelectType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    type = res.getJSONArray(position).getString(0);
                    if (type.equals("0")) {
                        type_val = "";
                        dialog.dismiss();
                        mTvRoomType.setBackgroundResource(R.drawable.select_type);
                    } else if (type.equals("1")) {
                        LiveCommon.showInputContentDialog(ReadyStartLiveActivity.this, "请设置房间密码"
                                , new DialogInterface() {
                                    @Override
                                    public void cancelDialog(View v, Dialog d) {
                                        d.dismiss();
                                        type = "0";
                                        type_val = "";
                                    }

                                    @Override
                                    public void determineDialog(View v, Dialog d) {

                                        EditText e = (EditText) d.findViewById(R.id.et_input);
                                        if (TextUtils.isEmpty(e.getText().toString())) {
                                            showToast3("密码不能为空", 0);
                                            return;
                                        }
                                        type = "1";
                                        type_val = e.getText().toString();
                                        mTvRoomType.setBackgroundResource(R.drawable.select_type1);
                                        d.dismiss();
                                    }
                                });
                        dialog.dismiss();
                    } else if (type.equals("2")) {
                        LiveCommon.showInputContentDialog(ReadyStartLiveActivity.this, "请设置收费金额(收益以直播结束显示为准)"
                                , new DialogInterface() {
                                    @Override
                                    public void cancelDialog(View v, Dialog d) {
                                        d.dismiss();
                                        type_val = "";
                                        type = "2";
                                    }

                                    @Override
                                    public void determineDialog(View v, Dialog d) {
                                        EditText e = (EditText) d.findViewById(R.id.et_input);
                                        if (TextUtils.isEmpty(e.getText().toString())) {
                                            showToast3("金额不能为空", 0);
                                            return;
                                        }
                                        if (e.getText().toString().equals("0")) {
                                            showToast3("请输入正确的金额", 0);
                                            return;
                                        }
                                        type = "2";
                                        type_val = e.getText().toString();
                                        mTvRoomType.setBackgroundResource(R.drawable.select_type2);
                                        d.dismiss();
                                    }
                                });
                        dialog.dismiss();
                    } else if (type.equals("3")) {
                        final Dialog timeChargeDialog = new Dialog(ReadyStartLiveActivity.this, R.style.dialog);
                        timeChargeDialog.setContentView(R.layout.dialog_set_room_charge);
                        Window dialogWindow = timeChargeDialog.getWindow();
                        dialogWindow.setGravity(Gravity.CENTER);
                        timeChargeDialog.setCanceledOnTouchOutside(true);
                        ListView listView = (ListView) timeChargeDialog.findViewById(R.id.lv_ready_charge);
                        TextView mTvCancel = (TextView) timeChargeDialog.findViewById(R.id.btn_cancel);
                        TextView mTvConfirm = (TextView) timeChargeDialog.findViewById(R.id.btn_confirm);
                        listView.setAdapter(new ListViewAdapter());
                        mTvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                type = "0";
                                type_val = "";
                                timeChargeDialog.dismiss();
                            }
                        });
                        mTvConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TextUtils.isEmpty(type_val)) {
                                    showToast3("金额不能为空", 0);
                                    return;
                                }
                                type = "3";
                                mTvRoomType.setBackgroundResource(R.drawable.select_type3);
                                timeChargeDialog.dismiss();
                            }
                        });

                        timeChargeDialog.show();
                        dialog.dismiss();
                    } else if (type.equals("6")) {
                        LiveCommon.showInputContentDialog(ReadyStartLiveActivity.this, "请设置收费金额(收益以直播结束显示为准)"
                                , new DialogInterface() {
                                    @Override
                                    public void cancelDialog(View v, Dialog d) {
                                        d.dismiss();
                                        type_val = "";
                                        type = "6";
                                    }

                                    @Override
                                    public void determineDialog(View v, Dialog d) {
                                        EditText e = (EditText) d.findViewById(R.id.et_input);
                                        if (TextUtils.isEmpty(e.getText().toString())) {
                                            showToast3("金额不能为空", 0);
                                            return;
                                        }
                                        if (e.getText().toString().equals("0")) {
                                            showToast3("请输入正确的金额", 0);
                                            return;
                                        }
                                        type = "6";
                                        type_val = e.getText().toString();
                                        mTvRoomType.setBackgroundResource(R.drawable.sibo);
                                        d.dismiss();
                                    }
                                });
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class ListViewAdapter extends BaseAdapter {
        JSONArray res = AppConfig.LIVE_TIME_COIN;

        @Override
        public int getCount() {
            return res.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return res.getString(position);
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(AppContext.getInstance(), R.layout.item_ready_charge, null);
                viewHolder = new ViewHolder();

                viewHolder.mChargeCoin = (TextView) convertView.findViewById(R.id.tv_item_chargecoin);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            try {
                viewHolder.mChargeCoin.setText(res.getString(position) + AppConfig.CURRENCY_NAME + "/分钟");
                viewHolder.mChargeCoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.mChargeCoin.setTextColor(getResources().getColor(R.color.maintone));
                        try {
                            type_val = res.getString(position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (last_item != -1 && last_item != position) {
                            //如果已经单击过条目并且上次保存的item位置和当前位置不同
                            if (oldView != null)
                                oldView.setTextColor(getResources().getColor(R.color.black));//把上次选中的样式去掉
                        }
                        last_item = position;//把当前的位置保存下来
                        oldView = (TextView) v.findViewById(R.id.tv_item_chargecoin);//把当前的条目保存下来


                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return convertView;
        }

        class ViewHolder {
            TextView mChargeCoin;
        }
    }


    @Override
    public void initData() {
        mUser = AppContext.getInstance().getLoginUser();
        Glide.with(this).load(mUser.avatar_thumb).bitmapTransform(new BlurTransformation(ReadyStartLiveActivity.this, 25)).into(mIvBg);
    }

    @OnClick({R.id.iv_live_exit, R.id.btn_start_live, R.id.ready_start_live_normal_type,
            R.id.ready_start_live_private_type, R.id.ready_start_live_back})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_live://创建房间
                //请求服务端存储记录
                createRoom();
                break;
            case R.id.iv_live_exit:
                finish();
                break;
            case R.id.ready_start_live_normal_type:
                mNormalTypeRoom.setChecked(true);
                mPrivateTypeRoom.setChecked(false);
                mCostLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.ready_start_live_private_type:
                mNormalTypeRoom.setChecked(false);
                mPrivateTypeRoom.setChecked(true);
                mCostLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.ready_start_live_back:
                finish();
                break;
        }
    }

    /**
     * @dw 创建直播房间
     * 请求服务端添加直播记录,分享直播
     */
    private void createRoom() {
        if (shareType != -2) {
//            ShareUtils.share(ReadyStartLiveActivity.this, shareType, mUser);
            SHARE_MEDIA type;
            switch (shareType) {
                case 0:
                    ShareUtils.share(this, SHARE_MEDIA.SINA, mUser);
                    break;
                case 1:
                    ShareUtils.share(this, SHARE_MEDIA.WEIXIN, mUser);
                    break;
                case 3:
                    ShareUtils.share(this, SHARE_MEDIA.QQ, mUser);
                    break;
            }
        } else {
            readyStart();
        }
        isClickStartLive = true;
        InputMethodUtils.closeSoftKeyboard(this);
//        mStartLive.setEnabled(false);
//        mStartLive.setTextColor(getResources().getColor(R.color.white));
    }

    /**
     * @dw 准备直播
     */
    private void readyStart() {
        if (mUser.id == null || StringUtils.toInt(mUser.id) <= 0) {
            showToast3("请登录后开播", 0);
            return;
        }
        if (null == protraitPath) {
            showToast3("请选择封面", 0);
            return;
        }
        if (TextUtils.isEmpty(mStartLiveTitle.getText().toString())) {
            showToast3("请填写标题", 0);
            return;
        }
        stringTitle = mStartLiveTitle.getText().toString();

        if (mNormalTypeRoom.isChecked()) {
            type = "0";
            type_val = "";
        }
        if (mPrivateTypeRoom.isChecked()) {
            if (TextUtils.isEmpty(mTvRoomType.getText().toString())) {
                showToast3("金额不能为空", 0);
                return;
            }
            if (mTvRoomType.getText().toString().equals("0")) {
                showToast3("请输入正确的金额", 0);
                return;
            }
            type = "6";
            type_val = mTvRoomType.getText().toString();

        }
        //请求服务端
        PhoneLiveApi.createLive(mUser.id, mUser.avatar, mUser.avatar_thumb,
                StringUtils.getNewString(mStartLiveTitle.getText().toString()), mUser.token,
                mUser.user_nicename,
                protraitFile,
                type,
                type_val,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        AppContext.showToastAppMsg(ReadyStartLiveActivity.this, "开启直播失败,请退出重试- -!");
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(s);
                        if (res != null) {
                            try {
                                JSONObject data = res.getJSONObject(0);
                                AppConfig.USERLIST_TIME = data.getInt("userlist_time");
                                String goodnum = data.getJSONObject("liang").getString("name");
                                String vipType = data.getJSONObject("vip").getString("type");

                                StartLiveActivity.startLiveActivity(ReadyStartLiveActivity.this,
                                        data.getString("stream"),
                                        data.getString("barrage_fee"),
                                        data.getString("votestotal"),
                                        data.getString("push"),
                                        data.getString("chatserver"),
                                        isFrontCameraMirro, type, goodnum, vipType);


                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (isClickStartLive && shareType != 7) {
            readyStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // 判断权限请求是否通过
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startTakePhoto();
                    return;
                }
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("您已拒绝使用摄像头权限,无法使用摄像头拍照,请去设置中修改", 0);
                } else if (grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("您拒绝读取文件权限,无法上传图片,请到设置中修改", 0);
                }
                return;
            }
        }
    }

    /**
     * 选择图片裁剪
     */
    private void startImagePick() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        }
    }

    // 裁剪头像的绝对路径
    private Uri getUploadTempFile(Uri uri, String path) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            AppContext.showToastAppMsg(this, "无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String thePath;
        if (null == path) {
            thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

            // 如果是标准Uri
            if (StringUtils.isEmpty(thePath)) {
                thePath = ImageUtils.getAbsoluteImagePath(this, uri);
            }
        } else {
            thePath = path;
        }
        String ext = FileUtil.getFileFormat(thePath);
        ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = "micp_crop_" + timeStamp + "." + ext;
        // 裁剪头像的绝对路径
        protraitPath = FILE_SAVEPATH + cropFileName;
        protraitFile = new File(protraitPath);

//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("protraitPath",protraitPath);
//        editor.commit();

        cropUri = Uri.fromFile(protraitFile);
        return this.cropUri;
    }

    private void startTakePhoto() {
        Intent intent;
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = AppConfig.DEFAULT_SAVE_PHOTE_PATH;
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (StringUtils.isEmpty(savePath)) {
            AppContext.showToastShort("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String fileName = "micp" + timeStamp + ".jpg";// 照片命名
        File out = new File(savePath, fileName);
        origUri = Uri.fromFile(out);
        theLarge = savePath + fileName;// 该照片的绝对路径

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            origUri = FileProvider.getUriForFile(this, "com.micp.im.fileprovider", out);//通过FileProvider创建一个content类型的Uri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
            intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);//将拍取的照片保存到指定URI
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
        }
        startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    /**
     * 拍照后裁剪
     *
     * @param data 原始图片
     */
    private void startActionCrop(Uri data, String path) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", this.getUploadTempFile(data, path));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP);// 输出图片大小
        intent.putExtra("outputY", CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnIntent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                    startActionCrop(origUri, theLarge);// 拍照后裁剪
                    return;
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                    startActionCrop(imageReturnIntent.getData(), null);// 选图后裁剪
                    return;
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:

                    if (protraitFile != null) {
                        mIvLivePic.setImageBitmap(BitmapFactory.decodeFile(protraitPath));
                    }
                    return;
            }
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, imageReturnIntent);
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    /**
     * @param v    点击按钮
     * @param type 分享平台
     * @dw 开始直播分享
     */
    private void startLiveShare(View v, int type) {
        String titleStr = "";
        if (type == shareType) {
            String titlesClose[] = getResources().getStringArray(R.array.live_start_share_close);
            titleStr = titlesClose[type];
        } else {
            String titlesOpen[] = getResources().getStringArray(R.array.live_start_share_open);
            titleStr = titlesOpen[type];
        }

        View popView = getLayoutInflater().inflate(R.layout.pop_view_share_start_live, null);
        TextView title = (TextView) popView.findViewById(R.id.tv_pop_share_start_live_prompt);
        title.setText(titleStr);
        PopupWindow pop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        int location[] = new int[2];
        v.getLocationOnScreen(location);
        pop.setFocusable(false);

        pop.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.getWidth() / 2 - popView.getMeasuredWidth() / 2, location[1] - popView.getMeasuredHeight());
    }

    private class ListSelectTypeAdapter extends BaseAdapter {
        JSONArray res = AppConfig.LIVE_TYPE;

        @Override
        public int getCount() {
            return res.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return res.getString(position);
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(AppContext.getInstance(), R.layout.item_dialog_roomtype, null);
                viewHolder = new ViewHolder();

                viewHolder.mTvType = (TextView) convertView.findViewById(R.id.tv_type);
                viewHolder.mViewLine = convertView.findViewById(R.id.ll_live_line);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            try {
                viewHolder.mTvType.setText(res.getJSONArray(position).getString(1));
                if (position == res.length() - 1) {
                    viewHolder.mViewLine.setBackgroundResource(R.color.transparent);
                } else {
                    viewHolder.mViewLine.setBackgroundResource(R.color.light_gray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return convertView;
        }

        class ViewHolder {
            TextView mTvType;
            View mViewLine;
        }
    }





    //分享dialog 增加朋友圈 QQ空间分享
    public  void initShareDialog(final String title, final String describe, final String imageUrl, final String shareUrl) {
        final Dialog dialog = new Dialog(this, R.style.dialog_normal);

        final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_share, null);
        dialogView.findViewById(R.id.dialog_share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(ReadyStartLiveActivity.this, SHARE_MEDIA.WEIXIN, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.dialog_share_wechat_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(ReadyStartLiveActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.dialog_share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(ReadyStartLiveActivity.this, SHARE_MEDIA.QQ, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();

            }
        });
        dialogView.findViewById(R.id.dialog_share_qzone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(ReadyStartLiveActivity.this, SHARE_MEDIA.QZONE, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();

            }
        });
        dialogView.findViewById(R.id.dialog_share_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(ReadyStartLiveActivity.this, SHARE_MEDIA.SINA, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.dialog_share_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(dialogView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
    }

}
