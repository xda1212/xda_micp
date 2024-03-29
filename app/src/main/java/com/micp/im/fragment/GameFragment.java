package com.micp.im.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.adapter.GameListAdapter;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.BaseFragment;
import com.micp.im.bean.LiveJson;
import com.micp.im.ui.VideoPlayerActivity;
import com.micp.im.ui.other.OnItemEvent;
import com.micp.im.utils.StringUtils;
import com.micp.im.utils.TLog;
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
 * 首页最新直播
 */
public class GameFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    List<LiveJson> mUserList = new ArrayList<>();

    @InjectView(R.id.recommend_list)
    ListView mGameLiveView;

    @InjectView(R.id.refreshLayout)
    SwipeRefreshLayout mRefresh;
    //默认提示
    @InjectView(R.id.fensi)
    LinearLayout mFensi;

    @InjectView(R.id.load)
    LinearLayout mLoad;

    private GameListAdapter gameAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index_hot, null);

        ButterKnife.inject(this, view);
        initData();
        initView(view);
        return view;
    }

    @Override
    public void initData() {
        gameAdapter = new GameListAdapter(getActivity().getLayoutInflater(), mUserList);
        mGameLiveView.setAdapter(gameAdapter);//BBB
    }

    @Override
    public void initView(View view) {
        mGameLiveView.setOnItemClickListener(new OnItemEvent(1000) {
            @Override
            public void singleClick(View v, int position) {
                if (AppContext.getInstance().getLoginUid() == null || StringUtils.toInt(AppContext.getInstance().getLoginUid()) == 0) {
                    Toast.makeText(getContext(), "请登录..", Toast.LENGTH_SHORT).show();
                    return;
                }
                VideoPlayerActivity.startVideoPlayerActivity(getContext(), mUserList.get(position));
            }

        });
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.global));
        mRefresh.setOnRefreshListener(this);
    }

    //最新主播数据请求
    private void requestData() {
        PhoneLiveApi.getGameUserList(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

                if (mRefresh != null) {

                    mRefresh.setRefreshing(false);
                    mFensi.setVisibility(View.GONE);
                    mLoad.setVisibility(View.VISIBLE);
                    mGameLiveView.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onResponse(String response, int id) {
                if (mRefresh != null) {
                    mRefresh.setRefreshing(false);
                }
                JSONArray resUserListJsonArr = ApiUtils.checkIsSuccess(response);
                TLog.error("resUserListJsonArr" + response);
                if (null != resUserListJsonArr) {
                    try {
                        mUserList.clear();
                        Gson g = new Gson();
                        for (int i = 0; i < resUserListJsonArr.length(); i++) {
                            mUserList.add(g.fromJson(resUserListJsonArr.getString(i), LiveJson.class));
                        }
                        if (mUserList.size() > 0) {
                            fillUI();
                        } else {
                            if (mFensi != null) {
                                mFensi.setVisibility(View.VISIBLE);
                            }
                            if (mLoad != null) {
                                mLoad.setVisibility(View.GONE);
                            }
                            if (mGameLiveView != null) {
                                mGameLiveView.setVisibility(View.INVISIBLE);
                            }

                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                } else {
                    if (mFensi != null) {
                        mFensi.setVisibility(View.VISIBLE);
                    }
                    if (mLoad != null) {
                        mLoad.setVisibility(View.GONE);
                    }
                    if (mGameLiveView != null) {
                        mGameLiveView.setVisibility(View.INVISIBLE);
                    }

                }

            }
        });
    }

    private void fillUI() {
        if (mFensi != null) {
            mFensi.setVisibility(View.GONE);
        }
        if (mLoad != null) {
            mLoad.setVisibility(View.GONE);
        }
        if (mGameLiveView != null) {
            mGameLiveView.setVisibility(View.VISIBLE);
        }


        gameAdapter.notifyDataSetChanged();


    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getGameLive");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}
