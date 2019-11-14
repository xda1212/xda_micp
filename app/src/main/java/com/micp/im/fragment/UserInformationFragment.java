package com.micp.im.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.micp.im.AppConfig;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.adapter.MineAdapter;
import com.micp.im.adapter.UserInformationVideoAdapter;
import com.micp.im.adapter.VideoAdapter2;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.BaseFragment;
import com.micp.im.bean.ActiveBean;
import com.micp.im.bean.UserBean;
import com.micp.im.bean.UserInfo;
import com.micp.im.event.CoinNameEvent;
import com.micp.im.ui.customviews.LineControllerView;
import com.micp.im.utils.LiveUtils;
import com.micp.im.utils.LoginUtils;
import com.micp.im.utils.StringUtils;
import com.micp.im.utils.UIHelper;
import com.micp.im.widget.AvatarView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 登录用户中心页面
 */
public class UserInformationFragment extends BaseFragment {

    //头像
    @InjectView(R.id.iv_avatar)
    AvatarView mIvAvatar;
    //昵称
    @InjectView(R.id.tv_name)
    TextView mTvName;

    @InjectView(R.id.ll_user_container)
    View mUserContainer;

    //退出登陆
    @InjectView(R.id.ll_loginout)
    LinearLayout mLoginOut;

    //直播记录
    @InjectView(R.id.tv_info_u_live_num)
    TextView mLiveNum;

    //关注
    @InjectView(R.id.tv_info_u_follow_num)
    TextView mFollowNum;

    //粉丝
    @InjectView(R.id.tv_info_u_fans_num)
    TextView mFansNum;

    //id
    @InjectView(R.id.tv_id)
    TextView mUId;

    @InjectView(R.id.iv_sex)
    ImageView mIvSex;

    @InjectView(R.id.iv_level)
    TextView mIvLevel;

    @InjectView(R.id.iv_anchor_level)
    TextView mIvAnchorLevel;

    @InjectView(R.id.ll_profit)
    LineControllerView mLcProfit;

    @InjectView(R.id.ll_family)
    LineControllerView mLlFamily;

    @InjectView(R.id.ll_family_manage)
    LineControllerView mLlFamilyManage;

    @InjectView(R.id.ll_distribution)
    LineControllerView mLlDistribution;

    private UserBean mInfo;

    private LineControllerView mCoinName;

    private int mPage;
    private Gson mGson;
    private Type mType;
    private Type listType;
    private UserInformationVideoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ListView listView;
    private MineAdapter mineAdapter;
    private List<UserInfo.ListBean> listData  =new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_information,
                container, false);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mInfo = AppContext.getInstance().getLoginUser();
        fillUI();
    }

    @Override
    public void initData() {
        mGson = new Gson();
        mType = new TypeToken<List<ActiveBean>>() {
        }.getType();
        listType = new TypeToken<List<UserInfo.ListBean>>(){}.getType();
    }

    @Override
    public void initView(View view) {
        view.findViewById(R.id.ll_live).setOnClickListener(this);
        view.findViewById(R.id.ll_following).setOnClickListener(this);
        view.findViewById(R.id.ll_fans).setOnClickListener(this);
        view.findViewById(R.id.ll_profit).setOnClickListener(this);
        view.findViewById(R.id.ll_setting).setOnClickListener(this);
        view.findViewById(R.id.ll_level).setOnClickListener(this);
        mCoinName = view.findViewById(R.id.ll_diamonds);
        mCoinName.setOnClickListener(this);
        view.findViewById(R.id.ll_about).setOnClickListener(this);
        view.findViewById(R.id.ll_authenticate).setOnClickListener(this);
        view.findViewById(R.id.iv_avatar).setOnClickListener(this);
        view.findViewById(R.id.tv_edit_info).setOnClickListener(this);
        view.findViewById(R.id.ll_shopthings).setOnClickListener(this);
        view.findViewById(R.id.ll_shop).setOnClickListener(this);
        view.findViewById(R.id.ll_distribution).setOnClickListener(this);
        view.findViewById(R.id.ll_family).setOnClickListener(this);
        view.findViewById(R.id.ll_family_manage).setOnClickListener(this);
        view.findViewById(R.id.ll_details).setOnClickListener(this);
        view.findViewById(R.id.ll_qrcode).setOnClickListener(this);
//        view.findViewById(R.id.ll_video).setOnClickListener(this);
        mRecyclerView = view.findViewById(R.id.user_information_video_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        mLoginOut.setOnClickListener(this);

        listView = view.findViewById(R.id.lv_mine);

        EventBus.getDefault().register(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (listData.get(position).getId().equals("1")) {   //我的收益
                    UIHelper.showProfitActivity(getActivity());
                }else if (listData.get(position).getId().equals("2")){ //我的M币
                    UIHelper.showMyDiamonds(getActivity());
                }


                else if (listData.get(position).getId().equals("3")) {  //我的等级
                    UIHelper.showWebView(getContext(), listData.get(position).getHref()+"&uid=" + mInfo.id
                            + "&token=" + AppContext.getInstance().getToken(), "");
                }
                else if (listData.get(position).getId().equals("14")){ //我的明细
                    UIHelper.showWebView(getContext(), listData.get(position).getHref()+"&uid=" + mInfo.id
                            + "&token=" + AppContext.getInstance().getToken(), "");

                    UIHelper.showWebView(getContext(), AppConfig.MAIN_URL
                            + "/index.php?g=Appapi&m=Detail&a=index&uid=" + mInfo.id
                            + "&token=" + AppContext.getInstance().getToken(), "");
                }
                else if (listData.get(position).getId().equals("13")){
                    UIHelper.showSetting(getActivity());
                }
                else{

                    UIHelper.showWebView(getContext(), listData.get(position).getHref(), listData.get(position).getName());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinNameEvent(CoinNameEvent e) {
        mCoinName.setName("我的" + e.getCoinName());
    }

    private void fillUI() {
        if (mInfo == null)
            return;


//        Glide.with(this).load(mInfo.avatar).placeholder(R.drawable.icon_def_qq).into(mIvAvatar);
        mIvAvatar.setAvatarUrl(mInfo.avatar);
        //昵称
        mTvName.setText(mInfo.user_nicename);


        mIvSex.setImageResource(LiveUtils.getSexRes(mInfo.sex));
        mIvLevel.setText("V" + mInfo.level);
        mIvAnchorLevel.setText("M" + mInfo.level_anchor);
    }

    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {

            sendRequestData();
        }

    }

    private void sendRequestData() {
        String uid  = AppContext.getInstance().getLoginUid();
        String token = AppContext.getInstance().getToken();
        PhoneLiveApi.getMyUserInfo(AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(), stringCallback);

        mPage = 1;
        PhoneLiveApi.getMyVideo(1, AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (mAdapter != null) {
                    mAdapter.clear();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("200".equals(obj.getString("ret"))) {
                        JSONObject data = obj.getJSONObject("data");
                        if (0 == data.getInt("code")) {
                            List<ActiveBean> list = mGson.fromJson(data.getString("info"), mType);
                            if (list.size() > 0) {
                                if (mAdapter == null) {
                                    mAdapter = new UserInformationVideoAdapter(getContext(), list);

                                    mRecyclerView.setAdapter(mAdapter);
                                } else {
                                    mAdapter.setData(list);
                                }
                            } else {
                                if (null != mAdapter) {
                                    mAdapter.clear();
                                }
                            }
                        } else {
                            AppContext.toast(data.getString("msg"));
                        }
                    } else {
                        AppContext.toast("获取数据失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private StringCallback stringCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String s, int id) {
            JSONArray res = ApiUtils.checkIsSuccess(s);
            if (res != null) {
                try {
                    JSONObject object = res.getJSONObject(0);
                    mInfo = new Gson().fromJson(object.toString(), UserBean.class);
                    AppContext.getInstance().updateUserInfo(mInfo);

                    mLiveNum.setText(object.getString("lives"));
                    mFollowNum.setText(object.getString("follows"));
                    mFansNum.setText(object.getString("fans"));
                    int goodnum = StringUtils.toInt(object.getJSONObject("liang").getString("name"));
                    if (goodnum != 0) {
                        mUId.setText("靓:" + goodnum);
                    } else {
                        mUId.setText("ID:" + mInfo.id);
                    }
//                    if (object.getString("agent_switch").equals("1")) {
//                        mLlDistribution.setVisibility(View.VISIBLE);
//                    }
//                    if (object.getString("family_switch").equals("1")) {
//                        mLlFamily.setVisibility(View.VISIBLE);
//                        mLlFamilyManage.setVisibility(View.VISIBLE);
//                    }

                    listData =  mGson.fromJson(object.getString("list"),listType);
                    mineAdapter = new MineAdapter(getActivity().getLayoutInflater(),listData);
                    listView.setAdapter(mineAdapter);
                    setListViewHeightBasedOnChildren(listView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    };

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.ll_authenticate://申请认证
                UIHelper.showWebView(getActivity(),
                        AppConfig.MAIN_URL + "/index.php?g=Appapi&m=auth&a=index&uid=" +
                                mInfo.id + "&token=" + AppContext.getInstance().getToken(), "");
                break;
            case R.id.iv_avatar:
                UIHelper.showHomePageActivity(getContext(), mInfo.id);
                break;
            case R.id.ll_live:
                //"我的"中 直播只显示直播次数 不再跳转详细界面 2019。9.24
//                UIHelper.showLiveRecordActivity(getActivity(), mInfo.id);
                break;
            case R.id.ll_following:
                UIHelper.showAttentionActivity(getActivity(), mInfo.id);
                break;
            case R.id.ll_fans:
                UIHelper.showFansActivity(getActivity(), mInfo.id);
                break;
            case R.id.ll_setting:
                UIHelper.showSetting(getActivity());
                break;
            case R.id.ll_diamonds:
                //我的钻石
                UIHelper.showMyDiamonds(getActivity());
                break;
            case R.id.ll_level:
                //我的等级
                UIHelper.showWebView(getContext(), AppConfig.MAIN_URL + "/index.php?g=Appapi&m=level&a=index&uid=" + mInfo.id + "&token=" + AppContext.getInstance().getToken(), "");
                break;

            //退出登录
            case R.id.ll_loginout:
                LoginUtils.outLogin(getActivity());
                getActivity().finish();
                break;

            case R.id.ll_profit:
                //收益
                UIHelper.showProfitActivity(getActivity());
                break;
            //编辑资料
            case R.id.tv_edit_info:
                UIHelper.showMyInfoDetailActivity(getContext());
                break;
            case R.id.ll_about:
                UIHelper.showWebView(getContext(), AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=lists", "");
                break;
            case R.id.ll_shop:
                UIHelper.shoShopActivity(getContext());
                break;
            case R.id.ll_shopthings:
                UIHelper.shoShopThingsActivity(getContext());
                break;
            case R.id.ll_family:
                UIHelper.showWebView(getContext(), AppConfig.MAIN_URL + "/index.php?g=Appapi&m=Family&a=index2&uid=" + mInfo.id + "&token=" + AppContext.getInstance().getToken(), "");
                break;
            case R.id.ll_family_manage:
                UIHelper.showWebView(getContext(), AppConfig.MAIN_URL + "/index.php?g=Appapi&m=Family&a=home&uid=" + mInfo.id + "&token=" + AppContext.getInstance().getToken(), "");
                break;
            case R.id.ll_distribution:
                UIHelper.showWebView(getContext(), AppConfig.MAIN_URL + "/index.php?g=Appapi&m=Agent&a=index&uid=" + mInfo.id + "&token=" + AppContext.getInstance().getToken(), "");
                break;
            case R.id.ll_details:
                UIHelper.showWebView(getContext(), AppConfig.MAIN_URL + "/index.php?g=Appapi&m=Detail&a=index&uid=" + mInfo.id + "&token=" + AppContext.getInstance().getToken(), "");
                break;
            case R.id.ll_qrcode:
                UIHelper.showWebView(getContext(), AppConfig.MAIN_URL + "/index.php?g=Appapi&m=UserInfo&a=index&user_id=" + mInfo.id, "我的邀请");
                break;
//            case R.id.ll_video:
//                UIHelper.shoMyVideoActivity(getContext());
//                UIHelper.showHomePageActivity(getContext(), AppContext.getInstance().getLoginUser().id);
            default:
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        sendRequestData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
