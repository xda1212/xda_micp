package com.micp.im.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.adapter.GameListAdapter;
import com.micp.im.adapter.LiveUserAdapter;
import com.micp.im.adapter.RLiveUserAdapter;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.BaseFragment;
import com.micp.im.bean.LiveJson;
import com.micp.im.ui.HomePageActivity;
import com.micp.im.ui.OrderWebViewActivity;
import com.micp.im.ui.RankingWebViewActivity;
import com.micp.im.ui.VideoPlayerActivity;
import com.micp.im.ui.customviews.ViewPagerIndicator;
import com.micp.im.ui.other.OnItemEvent;
import com.micp.im.utils.StringUtils;
import com.micp.im.utils.TLog;
import com.micp.im.utils.UIHelper;
import com.tuanmai.tools.toast.ToastUtil;
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
public class NewestLiveFragment extends BaseFragment  {

    List<LiveJson> mUserList = new ArrayList<>();

    /*@InjectView(R.id.recommend_list)
    ListView mGameLiveView;*/
    @InjectView(R.id.recommend_rl)
    RecyclerView recyclerView;

    /*@InjectView(R.id.refreshLayout)
    SwipeRefreshLayout mRefresh;*/

    @InjectView(R.id.refreshLayout1)
    PullToRefreshLayout mRefresh1;
    //默认提示
    @InjectView(R.id.fensi)
    LinearLayout mFensi;

    @InjectView(R.id.load)
    LinearLayout mLoad;

    @InjectView(R.id.live_tab_layout)
    RadioGroup mTabLayout;

    /*private LiveUserAdapter gameAdapter;*/

    private RLiveUserAdapter rLiveUserAdapter;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;

    public static String roomType ="0";
    private boolean isRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newest_live, null);
        ButterKnife.inject(this, view);
        isRefresh = true;
        initData();
        initView(view);
        return view;
    }




    @Override
    public void initData() {

        requestData(roomType);

      /*  gameAdapter = new LiveUserAdapter(getActivity().getLayoutInflater(), mUserList);*/
       /* mGameLiveView.setAdapter(gameAdapter);//BBB*/

        rLiveUserAdapter = new RLiveUserAdapter(getActivity().getLayoutInflater(),mUserList);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(rLiveUserAdapter);

    }

    @Override
    public void initView(View view) {
//        mGameLiveView.setOnItemClickListener(new OnItemEvent(1000) {
//            @Override
//            public void singleClick(View v, int position) {
//                if (AppContext.getInstance().getLoginUid() == null || StringUtils.toInt(AppContext.getInstance().getLoginUid()) == 0) {
//                    Toast.makeText(getContext(), "请登录..", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (mUserList.get(position).type.equals("6")) {
//                    UIHelper.showHomePageActivity(getContext(), mUserList.get(position).uid);
//                } else {
//                    VideoPlayerActivity.liveUid = mUserList.get(position).uid;
//                    VideoPlayerActivity.liveThumb = mUserList.get(position).thumb;
////                    VideoPlayerActivity.liveContext = mUserList.get(position).title;
//                    VideoPlayerActivity.startVideoPlayerActivity(getContext(), mUserList.get(position));    //进入直播页面
//                }
//            }
//
//        });





//        scrollView.getViewTreeObserver().addOnScrollChangedListener(new  ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                pullToRefreshLayout.setEnabled(scrollView.getScrollY()==0);
//            }
//        });


        mRefresh1.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束刷新
                        isRefresh = true;
                        requestData(roomType);
                        mRefresh1.finishRefresh();
                    }
                }, 500);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束加载更多
                        isRefresh = false;
                        requestData(roomType);
                        mRefresh1.finishLoadMore();
                    }
                }, 500);
            }
        });


        rLiveUserAdapter.setOnitemClickLintener(new RLiveUserAdapter.OnitemClick() {
            @Override
            public void onItemClick(int position) {
                //recycleview 点击事件
                if (AppContext.getInstance().getLoginUid() == null || StringUtils.toInt(AppContext.getInstance().getLoginUid()) == 0) {
                    Toast.makeText(getContext(), "请登录..", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mUserList.get(position).type.equals("6")) {
                    UIHelper.showHomePageActivity(getContext(), mUserList.get(position).uid);
                } else {
                    VideoPlayerActivity.liveUid = mUserList.get(position).uid;
                    VideoPlayerActivity.liveThumb = mUserList.get(position).thumb;
//                    VideoPlayerActivity.liveContext = mUserList.get(position).title;
                    VideoPlayerActivity.startVideoPlayerActivity(getContext(), mUserList.get(position));    //进入直播页面
                }
            }
        });


/*
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.global));
        mRefresh.setOnRefreshListener(this);*/

      /*  mRefresh1.setColorSchemeColors(getResources().getColor(R.color.global));
        mRefresh1.setOnRefreshListener(this);*/

        view.findViewById(R.id.btn_rank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderWebViewActivity.startOrderWebView(AppContext.getInstance(), AppContext.getInstance().getLoginUser().id);
            }
        });


        mTabLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
//                pageIndex = 1;
                if (id == R.id.live_tab_normal_room) {
                    roomType = "0";
                    requestData(roomType);
                } else if (id == R.id.live_tab_vip_room) {
                    roomType = "6";
                    requestData(roomType);
                }
            }
        });
    }
    int page = 1;

    //最新主播数据请求
    private void requestData(String type) {
        if (isRefresh){
            page = 1;
        }else{
            page++;
        }
        PhoneLiveApi.getNewestUserList(page+"",type,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
             /*   if (mRefresh != null) {
                    mRefresh.setRefreshing(false);
                    mFensi.setVisibility(View.GONE);
                    mLoad.setVisibility(View.VISIBLE);
                    mGameLiveView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);//
                }*/

             /*   if (mRefresh1 != null) {
                    mRefresh1.setRefreshing(false);
                    mFensi.setVisibility(View.GONE);
                    mLoad.setVisibility(View.VISIBLE);
                    *//*mGameLiveView.setVisibility(View.INVISIBLE);*//*
                    recyclerView.setVisibility(View.INVISIBLE);//
                }*/

            }

            @Override
            public void onResponse(String response, int id) {
              /*  if (mRefresh != null) {
                    mRefresh.setRefreshing(false);
                }*/

               /* if (mRefresh1 != null) {
                    mRefresh1.setRefreshing(false);
                }*/
                JSONArray resUserListJsonArr = ApiUtils.checkIsSuccess(response);
                TLog.error("resUserListJsonArr" + response);
                if (null != resUserListJsonArr) {
                    try {

                        if (isRefresh){
                            mUserList.clear();
                        }

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
                          /*  if (mGameLiveView != null) {
                                mGameLiveView.setVisibility(View.INVISIBLE);
                            }*/

                            if (recyclerView != null) {//recyclerview
                                recyclerView.setVisibility(View.INVISIBLE);
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
                   /* if (mGameLiveView != null) {
                        mGameLiveView.setVisibility(View.INVISIBLE);
                    }*/
                    if (recyclerView != null) {//
                        recyclerView.setVisibility(View.INVISIBLE);
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
    /*    if (mGameLiveView != null) {
//            mGameLiveView.setVisibility(View.VISIBLE);    //2019.9.29设置listview不可见 测试recyclerview
            mGameLiveView.setVisibility(View.INVISIBLE);
        }*/

        if (recyclerView != null) { //
            recyclerView.setVisibility(View.VISIBLE);
        }


        /*gameAdapter.notifyDataSetChanged();*/
        rLiveUserAdapter.notifyDataSetChanged();


    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        roomType = null;
        OkHttpUtils.getInstance().cancelTag("getNewestUserList");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}
