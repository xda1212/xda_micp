package com.micp.im.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.adapter.CommunityListAdapter;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.BaseFragment;
import com.micp.im.bean.CommunityData;
import com.micp.im.utils.ShareUtils;
import com.micp.im.widget.SlideshowView;
import com.micp.im.widget.list.AutoLoadListView;
import com.micp.im.widget.list.PullToRefreshAutoLoadListView;
import com.micp.im.widget.list.pullrefresh.PullToRefreshBase;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * @author 魏鹏
 * @dw 首页推荐
 */
public class CommunityFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, AutoLoadListView.loadMoreListener {

    private int pageIndex = 1;
    private ViewType mCurrentType = ViewType.NEWEST;

    @InjectView(R.id.community_list)
    PullToRefreshAutoLoadListView mPullToRefreshView;
    //默认提示
    @InjectView(R.id.fensi)
    LinearLayout mLlFensi;
    @InjectView(R.id.load)
    LinearLayout mLoad;
    @InjectView(R.id.community_tab_layout)
    RadioGroup mTabLayout;
    private AutoLoadListView mListView;

    private List<CommunityData> mDataList = new ArrayList<>();

    private LayoutInflater inflater;

    private CommunityListAdapter mCommunityListAdapter;


    private String str="{\"ret\":200,\"data\":{\"code\":0,\"msg\":\"\",\"info\":[{\"slide_pic\":\"http:\\/\\/cdn.seatower.cn\\/20190607_5cfa23e4172cf.jpg\",\"slide_url\":\"\"},{\"slide_pic\":\"http:\\/\\/cdn.seatower.cn\\/20190607_5cfa1f6c64ee1.jpg\",\"slide_url\":\"http:\\/\\/www.seatower.cn\"}]},\"msg\":\"\"}";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, null);
        ButterKnife.inject(this, view);
        this.inflater = inflater;

        initView();
        initData();
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(str);
            JSONObject datajson = new JSONObject();
            datajson  =jsonObject.getJSONObject("data");
            int  code=datajson.getInt("code");

            JSONArray jsonArray =new JSONArray();
            jsonArray=datajson.getJSONArray("info");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }

    private void initView() {
        mPullToRefreshView.setScrollingWhileRefreshingEnabled(false);
        mPullToRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullToRefreshView.setOnRefreshListener(this);
        mListView = mPullToRefreshView.getRefreshableView();
        mListView.enableAutoLoadMore(getContext(), this);
        mListView.setSelector(R.color.white);
        mTabLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                pageIndex = 1;
                if (id == R.id.community_tab_new) {
                    mCurrentType = ViewType.NEWEST;
                    updateListData();
                } else if (id == R.id.community_tab_hot) {
                    mCurrentType = ViewType.HOT;
                    updateListData();
                } else if (id == R.id.community_tab_recommend) {
                    mCurrentType = ViewType.RECOMMEND;
                    updateListData();
                }
            }
        });
    }

    @Override
    public void initData() {
        //2016.09.06 无数据不显示轮播修改 wp
        mCommunityListAdapter = new CommunityListAdapter(getContext(), mDataList, true);
        mCommunityListAdapter.setListener(new CommunityListAdapter.CommunityListListener() {
            @Override
            public void showShareDialog(String title, String describe, String imageUrl, String shareUrl) {
                initShareDialog("我发现了一款宅男必备软件", "觅CP——男/女脱单交友必备APP，快来和我亲密互动吧。", imageUrl, shareUrl);
            }

        });
        mListView.setAdapter(mCommunityListAdapter);
    }


    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            mPullToRefreshView.onRefreshComplete();
            mLlFensi.setVisibility(View.GONE);
            mLoad.setVisibility(View.VISIBLE);
            mPullToRefreshView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResponse(String s, int id) {
            mPullToRefreshView.onRefreshComplete();
            JSONArray res = ApiUtils.checkIsSuccess(s);

            try {
                if (res != null) {
                    if (res.length() > 0) {
                        mListView.updateHasMore(true);
                    } else {
                        onLoadMoreFinish();
                    }
                    if (pageIndex == 1) {
                        mDataList.clear();
                    }

                    for (int i = 0; i < res.length(); i++) {
                        CommunityData data = new Gson().fromJson(res.getJSONObject(i).toString(), CommunityData.class);
                        if (null != data.video) {
                            data.video.setUserinfo(data.user);
                        }
                        mDataList.add(data);
                    }
                    fillUI();
                } else {
                    if (pageIndex == 1) {
                        mLlFensi.setVisibility(View.VISIBLE);
                        mLoad.setVisibility(View.GONE);
                        mPullToRefreshView.setVisibility(View.INVISIBLE);
                    } else {
                        pageIndex--;
                        mListView.updateHasMore(true);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void fillUI() {
        mLlFensi.setVisibility(View.GONE);
        mLoad.setVisibility(View.GONE);
        mPullToRefreshView.setVisibility(View.VISIBLE);

        mCommunityListAdapter.notifyDataSetChanged();
    }

    public void updateListData() {
        PhoneLiveApi.requestCommunityData(pageIndex, AppContext.getInstance().getLoginUser().id, mCurrentType, callback);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateListData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("selectTermsScreen");
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageIndex = 1;
        updateListData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        updateListData();
    }

    @Override
    public void onLoadMoreFinish() {
        mListView.loadMoreOnFinish();
    }

    @Override
    public void onLoadMoreFail() {
        mListView.loadMoreOnFail();
    }

    @Override
    public void onLoadMoreSuccessWithMore() {
        mListView.loadMoreOnSuccessWithMore();
    }

    public enum ViewType {
        NEWEST("newest"), HOT("hotest"), RECOMMEND("recommend"), MYSELF("self");

        private String mTypeValue;

        ViewType(String typeValue) {
            this.mTypeValue = typeValue;
        }

        public String getTypeValue() {
            return mTypeValue;
        }
    }


    //分享dialog 增加朋友圈 QQ空间分享
    private void initShareDialog(final String title, final String describe, final String imageUrl, final String shareUrl) {
        final Dialog dialog = new Dialog(getContext(), R.style.dialog_normal);

        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_share, null);
        dialogView.findViewById(R.id.dialog_share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(getActivity(), SHARE_MEDIA.WEIXIN, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.dialog_share_wechat_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.dialog_share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(getActivity(), SHARE_MEDIA.QQ, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();

            }
        });
        dialogView.findViewById(R.id.dialog_share_qzone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(getActivity(), SHARE_MEDIA.QZONE, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
                dialog.dismiss();

            }
        });
        dialogView.findViewById(R.id.dialog_share_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.share(getActivity(), SHARE_MEDIA.SINA, title, describe, AppContext.getInstance().getLoginUser(), imageUrl, shareUrl);
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
