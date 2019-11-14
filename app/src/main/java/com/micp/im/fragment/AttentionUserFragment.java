package com.micp.im.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.micp.im.R;
import com.micp.im.adapter.RecommendUserAdapter;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.BaseFragment;
import com.micp.im.bean.RecommendUser;
import com.micp.im.widget.WPSwipeRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * @author 魏鹏
 * @dw 首页推荐
 */
public class AttentionUserFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.recommend_list)
    ListView mListUserRoom;

    //默认提示
    @InjectView(R.id.fensi)
    LinearLayout mLlFensi;

    @InjectView(R.id.load)
    LinearLayout mLoad;

    @InjectView(R.id.refreshLayout)
    WPSwipeRefreshLayout mSwipeRefreshLayout;

    private List<RecommendUser> mUserList = new ArrayList<>();

    private LayoutInflater inflater;

    private RecommendUserAdapter mHotUserListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index_hot, null);
        ButterKnife.inject(this, view);
        this.inflater = inflater;

        initView();
        initData();

        return view;
    }

    private void initView() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.global));
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void initData() {
        //2016.09.06 无数据不显示轮播修改 wp
        mHotUserListAdapter = new RecommendUserAdapter(getContext(), getActivity().getLayoutInflater(), mUserList);
        mListUserRoom.setAdapter(mHotUserListAdapter);
    }


    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            mSwipeRefreshLayout.setRefreshing(false);
            mLlFensi.setVisibility(View.GONE);
            mLoad.setVisibility(View.VISIBLE);
            mListUserRoom.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResponse(String s, int id) {
            mSwipeRefreshLayout.setRefreshing(false);

            JSONArray res = ApiUtils.checkIsSuccess(s);

            try {
                if (res != null) {
                    mUserList.clear();
                    //直播数据
                    for (int i = 0; i < res.length(); i++) {
                        RecommendUser user = new Gson().fromJson(res.getJSONObject(i).toString(), RecommendUser.class);
                        mUserList.add(user);
                    }
                    fillUI();
                } else {
                    mLlFensi.setVisibility(View.VISIBLE);
                    mLoad.setVisibility(View.GONE);
                    mListUserRoom.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void fillUI() {
        mLlFensi.setVisibility(View.GONE);
        mLoad.setVisibility(View.GONE);
        mListUserRoom.setVisibility(View.VISIBLE);

        mHotUserListAdapter.notifyDataSetChanged();
    }

    private void updateLocation(){
        PhoneLiveApi.pushLocation(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                updateListData();
            }

            @Override
            public void onResponse(String s, int i) {
                updateListData();
            }
        });
    }

    public void updateListData() {
        PhoneLiveApi.requestAttentionDate(callback);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLocation();
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        updateLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("selectTermsScreen");
    }
}
