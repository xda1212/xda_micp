package com.micp.im.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.micp.im.AppConfig;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.adapter.CommunityListAdapter;
import com.micp.im.adapter.HomePageListAdapter;
import com.micp.im.adapter.LiveRecordAdapter;
import com.micp.im.adapter.VideoAdapter2;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.bean.ActiveBean;
import com.micp.im.bean.CommunityData;
import com.micp.im.bean.LiveRecordBean;
import com.micp.im.bean.UserBean;
import com.micp.im.bean.UserHomePageBean;
import com.micp.im.fragment.CommunityFragment;
import com.micp.im.fragment.GiftListDialogFragment;
import com.micp.im.interf.DialogInterface;
import com.micp.im.ui.customviews.RefreshLayout;
import com.micp.im.ui.dialog.LiveCommon;
import com.micp.im.utils.LiveUtils;
import com.micp.im.utils.ShareUtils;
import com.micp.im.utils.StringUtils;
import com.micp.im.utils.UIHelper;
import com.micp.im.widget.AvatarView;
import com.micp.im.widget.BlackTextView;
import com.tuanmai.tools.toast.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 他人信息
 */
public class HomePageActivity extends ToolBarBaseActivity implements RefreshLayout.OnRefreshListener {
    private Type mType;
    //昵称
    @InjectView(R.id.tv_home_page_uname)
    BlackTextView mUNice;

    @InjectView(R.id.iv_home_page_sex)
    ImageView mUSex;

    @InjectView(R.id.iv_home_page_level)
    ImageView mULevel;

    //头像
    @InjectView(R.id.av_home_page_uhead)
    AvatarView mUHead;

    //关注数
    @InjectView(R.id.tv_home_page_follow)
    BlackTextView mUFollowNum;

    //粉丝数
    @InjectView(R.id.tv_home_page_fans)
    BlackTextView mUFansNum;

    //个性签名
    @InjectView(R.id.tv_home_page_sign)
    BlackTextView mUSign;

    @InjectView(R.id.tv_home_page_sign2)
    BlackTextView mUSign2;


    @InjectView(R.id.ll_default_video)
    LinearLayout mDefaultVideoBg;

    @InjectView(R.id.tv_home_page_menu_follow)
    TextView mFollowState;

    @InjectView(R.id.tv_home_page_black_state)
    ImageView mTvBlackState;

    @InjectView(R.id.ll_home_page_bottom_menu)
    LinearLayout mLLBottomMenu;
    @InjectView(R.id.ll_home_page_menu_lahei)
    View mLiveButton;

    @InjectView(R.id.lv_live_record)
    ListView mLiveRecordList;
    @InjectView(R.id.lv_live_shiping)
    ListView mRecShiping;
    @InjectView(R.id.fensi)
    LinearLayout mFensi;

    @InjectView(R.id.load)
    LinearLayout mLoad;
    @InjectView(R.id.video_fensi)
    ImageView mVideoFensi;

    @InjectView(R.id.refreshLayout)
    RefreshLayout mRefreshLayout;
    @InjectView(R.id.rl_live_status)
    TextView mRlLiveStatusView;

    @InjectView(R.id.iv_home_page_anchorlevel)
    ImageView mULAnchorevel;

    @InjectView(R.id.tv_home_page_numid)
    BlackTextView mUNumId;

    @InjectView(R.id.tv_home_page_numn)
    BlackTextView mUNumN;

    @InjectView(R.id.home_birthday)
    TextView mBirthdayText;
    @InjectView(R.id.home_constellation)
    TextView mConstellationText;
    @InjectView(R.id.home_city)
    TextView mCityText;
    @InjectView(R.id.iv_more)
    ImageView mMore;

    private int mPage;
    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;

    public String uid;

    private AvatarView[] mOrderTopNoThree = new AvatarView[3];

    private UserHomePageBean mUserHomePageBean;

    private ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();
    private List<CommunityData> mVideoList = new ArrayList<>();
    private LiveRecordAdapter mLiveRecordAdapter;
    private HomePageListAdapter mVideoAdaper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView() {
        mLiveRecordList.setDividerHeight(1);
        mOrderTopNoThree[0] = (AvatarView) findViewById(R.id.av_home_page_order1);
        mOrderTopNoThree[1] = (AvatarView) findViewById(R.id.av_home_page_order2);
        mOrderTopNoThree[2] = (AvatarView) findViewById(R.id.av_home_page_order3);
        mLiveRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLiveRecordBean = mRecordList.get(i);
                //开始播放
                showLiveRecord();
            }
        });
        mType = new TypeToken<List<ActiveBean>>() {
        }.getType();
        mLiveRecordAdapter = new LiveRecordAdapter(mRecordList);
        mLiveRecordList.setAdapter(mLiveRecordAdapter);
        mRefreshLayout.setScorllView(mRecShiping);
        mRefreshLayout.setOnRefreshListener(this);
        ((TextView) findViewById(R.id.tv_home_tick_order)).setText(AppConfig.TICK_NAME + "排行榜");

        mVideoAdaper = new HomePageListAdapter(HomePageActivity.this, mVideoList, true);
        mVideoAdaper.setListener(new HomePageListAdapter.CommunityListListener() {
            @Override
            public void showShareDialog(String title, String describe, String imageUrl, String shareUrl) {
                initShareDialog("我发现了一款宅男必备软件", "觅CP--男/女脱单交友必备APP，快来和我亲密互动吧。", imageUrl, shareUrl);
            }
        });
        mRecShiping.setAdapter(mVideoAdaper);
    }

    @Override
    public void initData() {
        mPage = 1;
        uid = getIntent().getStringExtra("uid");

        if (uid.equals(getUserID())) {
            mLLBottomMenu.setVisibility(View.GONE);
            mLiveButton.setVisibility(View.GONE);
        }
        requestData();
        reuqestVideoData();
    }

    private void requestData() {
        //请求用户信息
        PhoneLiveApi.getHomePageUInfo(getUserID(), uid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mRefreshLayout.completeRefresh();
            }

            @Override
            public void onResponse(String response, int id) {
                mRefreshLayout.completeRefresh();
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (res != null) {

                    try {
                        mUserHomePageBean = new Gson().fromJson(res.getString(0), UserHomePageBean.class);
                        int goodnum = StringUtils.toInt(res.getJSONObject(0).getJSONObject("liang").getString("name"));
                        if (goodnum != 0) {
                            mUNumN.setText("靓");
                            mUNumId.setText(goodnum + "");
                        } else {
                            mUNumN.setText("ID");
                            mUNumId.setText(mUserHomePageBean.id + "");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fillUIUserInfo();
                }
            }
        });
    }

    //ui填充
    private void fillUIUserInfo() {
        if (mUserHomePageBean.islive.equals("1")) {
            mRlLiveStatusView.setVisibility(View.VISIBLE);
            mRlLiveStatusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppContext.getInstance().getLoginUid() == null || StringUtils.toInt(AppContext.getInstance().getLoginUid()) == 0) {
                        Toast.makeText(HomePageActivity.this, "请登录..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (mUserHomePageBean.liveinfo.type.equals("6")) {
                        UIHelper.shoPersonVideoActivity(HomePageActivity.this, mUserHomePageBean.liveinfo);
                    } else {
                        VideoPlayerActivity.startVideoPlayerActivity(HomePageActivity.this, mUserHomePageBean.liveinfo);
                    }
                }
            });
        } else {
            mRlLiveStatusView.setVisibility(View.GONE);
        }
        mUHead.setAvatarUrl(mUserHomePageBean.avatar_thumb);
        mUNice.setText(mUserHomePageBean.user_nicename);
        mUSex.setImageResource(LiveUtils.getSexRes(mUserHomePageBean.sex));
        mULevel.setImageResource(LiveUtils.getLevelRes(mUserHomePageBean.level));
        mUFansNum.setText(getString(R.string.fans) + ":" + mUserHomePageBean.fans);
        mUFollowNum.setText(getString(R.string.attention) + ":" + mUserHomePageBean.follows);
        mULAnchorevel.setImageResource(LiveUtils.getAnchorLevelRes2(mUserHomePageBean.level));
        mUSign.setText(mUserHomePageBean.signature);
        mUSign2.setText(mUserHomePageBean.signature);
        mBirthdayText.setText(mUserHomePageBean.birthday);
        mConstellationText.setText(mUserHomePageBean.city);
        mCityText.setText(mUserHomePageBean.city);

        mFollowState.setText(StringUtils.toInt(mUserHomePageBean.isattention) == 0 ? getString(R.string.follow2) : getString(R.string.alreadyfollow));
        List<UserHomePageBean.ContributeBean> os = mUserHomePageBean.contribute;
        for (int i = 0; i < os.size(); i++) {
            mOrderTopNoThree[i].setAvatarUrl(os.get(i).getAvatar());
        }

        if (null != mUserHomePageBean.liverecord) {
            mRecordList.clear();
            mRecordList.addAll(mUserHomePageBean.liverecord);

            if (mRecordList.size() != 0) {
                mLiveRecordList.setVisibility(View.VISIBLE);
                mFensi.setVisibility(View.GONE);
                mLoad.setVisibility(View.GONE);
                mLiveRecordAdapter.notifyDataSetChanged();
            } else {
                mLiveRecordList.setVisibility(View.INVISIBLE);
                mFensi.setVisibility(View.VISIBLE);
                mLoad.setVisibility(View.GONE);
            }


        } else {
            mLiveRecordList.setVisibility(View.INVISIBLE);
            mFensi.setVisibility(View.VISIBLE);
            mLoad.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.ll_home_page_menu_lahei, R.id.ll_home_page_menu_privatechat, R.id.tv_home_page_menu_follow, R.id.rl_home_pager_yi_order, R.id.tv_home_page_follow,
            R.id.iv_home_page_back, R.id.tv_home_page_fans, R.id.home_page_send_present, R.id.av_home_page_uhead,R.id.iv_more})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home_page_menu_privatechat:
                openPrivateChat();
                break;
            case R.id.ll_home_page_menu_lahei:
//                pullTheBlack();
                sendLianmai();
                break;
            case R.id.tv_home_page_menu_follow:
                followUserOralready();
                break;
            case R.id.iv_home_page_back:
                finish();
                break;
            case R.id.tv_home_page_fans:
                UIHelper.showFansActivity(this, uid);
                break;
            case R.id.tv_home_page_follow:
                UIHelper.showAttentionActivity(this, uid);
                break;
            case R.id.rl_home_pager_yi_order://魅力值排行榜
                OrderWebViewActivity.startOrderWebView(this, uid);
                break;
            case R.id.home_page_send_present:
                GiftListDialogFragment mGiftListDialogFragment = new GiftListDialogFragment();
                mGiftListDialogFragment.show(getSupportFragmentManager(), "GiftListDialogFragment");
                break;
            case R.id.av_home_page_uhead:
                ArrayList<String> temp = new ArrayList<>();
                temp.add(mUserHomePageBean.avatar_thumb);
                UIHelper.showImageViewPagerActivity(this, temp, 0);
                break;

            case R.id.iv_more:
                final String[] items = {"加入黑名单","举报"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this)/*.setIcon(R.mipmap.ic_launcher)*/
                        .setTitle("请选择")
                        .setItems(items, new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {
//                                Toast.makeText(mContext, "你点击的内容为： " + items[i], Toast.LENGTH_LONG).show();
                                if (i==0){
//                                    ToastUtil.show(HomePageActivity.this,"加入黑名单");

                                    AlertDialog.Builder blacklist = new AlertDialog.Builder(HomePageActivity.this)/*.setIcon(R.mipmap.ic_launcher)*/.setTitle("提示")
                                            .setMessage("加入黑名单，你将再也不能收到对方的信息").setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                    //ToDo: 你想做的事情
                                                    Toast.makeText(HomePageActivity.this, "加入黑名单成功", Toast.LENGTH_SHORT).show();
                                                }


                                            }).setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                    //ToDo: 你想做的事情
                                                    Toast.makeText(HomePageActivity.this, "已取消", Toast.LENGTH_SHORT).show();
                                                }


                                            });
                                    blacklist.create().show();
                                }else if (i==1){

//                                    ToastUtil.show(HomePageActivity.this,"举报成功");
                                    Toast.makeText(HomePageActivity.this, "举报成功", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                builder.create().show();
                break;
        }
    }

    private void reuqestVideoData() {
        PhoneLiveApi.requestCommunityData(mPage, uid, CommunityFragment.ViewType.MYSELF, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mRefreshLayout.completeRefresh();
            }

            @Override
            public void onResponse(String response, int id) {
                mRefreshLayout.completeRefresh();
                mRefreshLayout.completeLoadMore();
                JSONArray res = ApiUtils.checkIsSuccess(response);

                try {
                    if (res != null && res.length() > 0) {
                        mRecShiping.setVisibility(View.VISIBLE);
                        mVideoFensi.setVisibility(View.GONE);

                        for (int i = 0; i < res.length(); i++) {
                            CommunityData data = new Gson().fromJson(res.getJSONObject(i).toString(), CommunityData.class);
                            if (null != data.video) {
                                data.video.setUserinfo(data.user);
                            }
                            mVideoList.add(data);
                        }
                        mVideoAdaper.notifyDataSetChanged();
                    } else {
                        if (mVideoList.size() == 0) {
                            mRecShiping.setVisibility(View.INVISIBLE);
                            mVideoFensi.setVisibility(View.VISIBLE);
                        } else {
                            AppContext.toast("已经没有更多数据了");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
//        PhoneLiveApi.getHomeVideoInfo(uid, mPage, new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                mRefreshLayout.completeRefresh();
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                mRefreshLayout.completeRefresh();
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(response);
//                    if ("200".equals(obj.getString("ret"))) {
//                        JSONObject data = obj.getJSONObject("data");
//                        if (0 == data.getInt("code")) {
//                            mVideoList = new Gson().fromJson(data.getString("info"), mType);
//                            if (mVideoList.size() != 0) {
//                                mRecShiping.setVisibility(View.VISIBLE);
//                                mVideoFensi.setVisibility(View.GONE);
//                                mVideoAdaper = new VideoAdapter2(HomePageActivity.this, mVideoList, true);
//                                mRecShiping.setAdapter(mVideoAdaper);
//                            } else {
//                                mRecShiping.setVisibility(View.INVISIBLE);
//                                mVideoFensi.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
    }

    //打开直播记录
    private void showLiveRecord() {

        showWaitDialog("正在获取回放...", false);

        PhoneLiveApi.getLiveRecordById(mLiveRecordBean.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                JSONArray res = ApiUtils.checkIsSuccess(response);

                if (res != null) {
                    try {
                        mLiveRecordBean.setVideo_url(res.getJSONObject(0).getString("url"));
                        VideoBackActivity.startVideoBack(HomePageActivity.this, mLiveRecordBean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private void sendLianmai() {
        if (null != mUserHomePageBean.liveinfo) {
            PhoneLiveApi.checkoutRoom(AppContext.getInstance().getLoginUid()
                    , AppContext.getInstance().getToken(), mUserHomePageBean.liveinfo.stream, mUserHomePageBean.liveinfo.uid, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                JSONObject res = new JSONObject(response);
                                if (Integer.parseInt(res.getString("ret")) == 200) {
                                    JSONObject dataJson = res.getJSONObject("data");
                                    String code = dataJson.getString("code");
                                    if (code.equals("700")) {

                                        //AppManager.getAppManager().finishAllActivity();
                                        Intent intent = new Intent(AppContext.getInstance(), PhoneLoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        AppContext.getInstance().startActivity(intent);

                                    } else if (code.equals("1009")) {
                                        LiveCommon.showPersonDialog(HomePageActivity.this, "1", "1", dataJson.get("msg").toString(), "", "取消", "", new DialogInterface() {
                                            @Override
                                            public void cancelDialog(View v, Dialog d) {
                                                d.dismiss();
                                            }

                                            @Override
                                            public void determineDialog(View v, Dialog d) {

                                            }
                                        });
                                    } else if (code.equals("0")) {
                                        final JSONArray jsonArray = dataJson.getJSONArray("info");
                                        final String type_val = jsonArray.getJSONObject(0).getString("type_val");
                                        final int type = jsonArray.getJSONObject(0).getInt("type");
                                        final String type_msg = jsonArray.getJSONObject(0).getString("type_msg");
                                        if (jsonArray.getJSONObject(0).getString("fee_type").equals("1")) {
                                            LiveCommon.showPersonDialog(HomePageActivity.this, "2", "1", type_msg, "", "取消", "继续", new DialogInterface() {
                                                @Override
                                                public void cancelDialog(View v, Dialog d) {
                                                    d.dismiss();
                                                }

                                                @Override
                                                public void determineDialog(View v, Dialog d) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putInt("type", type);
                                                    bundle.putString("type_val", type_val);
                                                    bundle.putParcelable("USER_INFO", mUserHomePageBean.liveinfo);
                                                    Intent intent = new Intent(HomePageActivity.this, VideoPlayerActivity.class);
                                                    intent.putExtra(VideoPlayerActivity.USER_INFO, bundle);
                                                    startActivityForResult(intent, 0);
                                                    d.dismiss();
                                                }
                                            });

                                        } else {
                                            LiveCommon.showPersonDialog(HomePageActivity.this, "2", "1", "您的余额不足\n请充值后重试", "", "取消", "去充值", new com.micp.im.interf.DialogInterface() {
                                                @Override
                                                public void cancelDialog(View v, Dialog d) {
                                                    d.dismiss();

                                                }

                                                @Override
                                                public void determineDialog(View v, Dialog d) {
                                                    d.dismiss();
                                                    UIHelper.showMyDiamonds(HomePageActivity.this);
                                                }
                                            });

                                        }

                                    } else {
                                        Toast.makeText(AppContext.getInstance(), dataJson.get("msg").toString(), Toast.LENGTH_LONG).show();

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
        }
    }

    //拉黑
    private void pullTheBlack() {// black list
        PhoneLiveApi.pullTheBlack(AppContext.getInstance().getLoginUid(), uid,
                AppContext.getInstance().getToken(),
                new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        AppContext.showToastAppMsg(HomePageActivity.this, "操作失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        if (null == res) return;
                        if (StringUtils.toInt(mUserHomePageBean.isblack) == 0) {
                            //第二个参数如果为true，则把用户加入到黑名单后双方发消息时对方都收不到；false，则我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
                            try {
                                EMClient.getInstance().contactManager().addUserToBlackList(String.valueOf(mUserHomePageBean.id), true);
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                EMClient.getInstance().contactManager().removeUserFromBlackList(String.valueOf(mUserHomePageBean.id));
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }

                        int isBlack = StringUtils.toInt(mUserHomePageBean.isblack);

                        mUserHomePageBean.isblack = (isBlack == 0 ? "1" : "0");

//                        mTvBlackState.setText(isBlack == 0 ? getString(R.string.relieveblack) : getString(R.string.pullblack));
                        showToast3(isBlack == 0 ? "拉黑成功" : "解除拉黑", 0);

                    }
                });
    }

    //私信
    private void openPrivateChat() {

        if (StringUtils.toInt(mUserHomePageBean.isblack2) == 1) {
            AppContext.showToastAppMsg(this, "你已被对方拉黑无法私信");
            return;
        }

        UserBean userBean = new UserBean();
        userBean.id = mUserHomePageBean.id;
        userBean.user_nicename = mUserHomePageBean.user_nicename;
        userBean.avatar = mUserHomePageBean.avatar;
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("user", userBean);
        startActivity(intent);

    }

    private void followUserOralready() {

        PhoneLiveApi.showFollow(getUserID(), uid, getUserToken(), new PhoneLiveApi.AttentionCallback() {
            @Override
            public void callback(boolean isAttention) {
                if (isAttention) {
                    mUserHomePageBean.isattention = "1";
                    mFollowState.setText(getString(R.string.alreadyfollow));
                } else {
                    mUserHomePageBean.isattention = "0";
                    mFollowState.setText(getString(R.string.follow2));
                }
            }
        });
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }


    @Override
    protected void onDestroy() {//BBB
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getHomePageUInfo");
        OkHttpUtils.getInstance().cancelTag("showFollow");
        OkHttpUtils.getInstance().cancelTag("getPmUserInfo");
        OkHttpUtils.getInstance().cancelTag("pullTheBlack");
        OkHttpUtils.getInstance().cancelTag("getHomePageUInfo");
        OkHttpUtils.getInstance().cancelTag("getLiveRecord");
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onLoadMore() {
        mPage++;
//        PhoneLiveApi.getHomeVideoInfo(uid, mPage, mLoadMoreData);
        reuqestVideoData();
    }

    private StringCallback mLoadMoreData = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            mRefreshLayout.completeRefresh();
        }

        @Override
        public void onResponse(String response, int id) {
            mRefreshLayout.completeRefresh();
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                if ("200".equals(obj.getString("ret"))) {
                    JSONObject data = obj.getJSONObject("data");
                    if (0 == data.getInt("code")) {
                        List<ActiveBean> moreVideoList = new ArrayList<>();
                        moreVideoList = new Gson().fromJson(data.getString("info"), mType);
                        if (moreVideoList.size() > 0) {
//                            mVideoAdaper.insertList(moreVideoList);
                        } else {
                            AppContext.toast("已经没有更多数据了");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void initShareDialog(final String title, final String describe, final String imageUrl, final String shareUrl) {
        final Dialog dialog = new Dialog(this, R.style.dialog_normal);

        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_share, null);
        dialogView.findViewById(R.id.dialog_share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(HomePageActivity.this, SHARE_MEDIA.WEIXIN, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.dialog_share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(HomePageActivity.this, SHARE_MEDIA.QQ, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();

            }
        });
        dialogView.findViewById(R.id.dialog_share_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(HomePageActivity.this, SHARE_MEDIA.SINA, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
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
