package com.micp.im.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.micp.im.AppConfig;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.adapter.RecommendUserAdapter;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.BaseFragment;
import com.micp.im.bean.RecommendUser;
import com.micp.im.widget.MyListView;
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
public class NearUserFragment extends BaseFragment /*implements SwipeRefreshLayout.OnRefreshListener*/ {
    @InjectView(R.id.recommend_list1)
    MyListView mListUserRoom;
//@InjectView(R.id.recommend_list)
//    ListView mListUserRoom1;

    //默认提示
    @InjectView(R.id.fensi)
    LinearLayout mLlFensi;

    @InjectView(R.id.load)
    LinearLayout mLoad;
//
//    @InjectView(R.id.refreshLayout)
//    WPSwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.pullToRefreshLayout)
    PullToRefreshLayout pullToRefreshLayout;

    @InjectView(R.id.scrollView)
    ScrollView scrollView;


    private List<RecommendUser> mUserList = new ArrayList<>();

    private LayoutInflater inflater;

    private RecommendUserAdapter mHotUserListAdapter;
    private boolean isRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index_near, null);
        ButterKnife.inject(this, view);
        this.inflater = inflater;
        isRefresh = true;
        initView();
        initData();

        return view;
    }

    private void initView() {
     /*   mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.global));
        mSwipeRefreshLayout.setOnRefreshListener(this);*/
//
//        mSwipeRefreshLayout.setVisibility(View.GONE);
//        mListUserRoom1.setVisibility(View.GONE);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new  ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                pullToRefreshLayout.setEnabled(scrollView.getScrollY()==0);
            }
        });


        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束刷新
                        isRefresh = true;
                        updateLocation(true);
                        pullToRefreshLayout.finishRefresh();
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
                        updateLocation(false);
                        pullToRefreshLayout.finishLoadMore();
                    }
                }, 500);
            }
        });




//        //上拉加载
//        loadMoreView = getLayoutInflater().inflate(R.layout.load_more, null);
//        loadMoreView.setVisibility(View.GONE);
//        mListUserRoom.addFooterView(loadMoreView);
//        mListUserRoom.setFooterDividersEnabled(false);
//
//
//        //设置滑动监听
//        mListUserRoom.setOnScrollListener(new AbsListView.OnScrollListener() {
//            int visibleLastIndex = 0;    //最后的可视项索引
//            int visibleItemCount;        // 当前窗口可见项总数
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                switch (scrollState) {
//                    //当不滚动的时候
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//                        int itemsLastIndex = mHotUserListAdapter.getCount() - 1;    //数据集最后一项的索引
//                        int lastIndex = itemsLastIndex + 1;                //加上底部的loadMoreView项
//                        //判断是否是最底部
//                        //if (view.getLastVisiblePosition() == (view.getCount()) - 1) { //或者
//                        if (visibleLastIndex == lastIndex) {
//                            loadMoreView.setVisibility(View.VISIBLE);
//                            mHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //加载网络数据
//                                    updateLocation(false);
//                                    Message msg = new Message();
//                                    msg.what = LOADMORE;
//                                    msg.arg1 = visibleLastIndex - visibleItemCount + 1;
//                                    mHandler.sendMessage(msg);
//                                }
//                            }, 2000);
//                        }
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                this.visibleItemCount = visibleItemCount;
//                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
//            }
//        });


    }

    @Override
    public void initData() {
        //2016.09.06 无数据不显示轮播修改 wp
        mHotUserListAdapter = new RecommendUserAdapter(getContext(), getActivity().getLayoutInflater(), mUserList);
        mListUserRoom.setAdapter(mHotUserListAdapter);
//        setListViewHeightBasedOnChildren(mListUserRoom);
        AppContext.page = 1;
    }


    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            /*mSwipeRefreshLayout.setRefreshing(false);*/
            mLlFensi.setVisibility(View.GONE);
            mLoad.setVisibility(View.VISIBLE);
            mListUserRoom.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResponse(String s, int id) {
            /*mSwipeRefreshLayout.setRefreshing(false);*/
            JSONArray res = ApiUtils.checkIsSuccess(s);

            try {
                if (res != null) {
                    if (isRefresh){
                        mUserList.clear();
                    }
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
        setListViewHeightBasedOnChildren(mListUserRoom);
    }

    private void updateLocation(Boolean isRefresh){
        if (isRefresh){
            AppContext.page = 1;  //下拉刷新 页数重置为1
        }else {
            AppContext.page ++; //上拉加载 页数加 1
        }
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

    private void updateListData() {
        PhoneLiveApi.requestNearDate(callback);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateLocation(true);
    }

//    //下拉刷新
//    @Override
//    public void onRefresh() {
//        updateLocation(true);
//    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("requestNearDate");
    }

    private static final int REFRESH = 0x01;
    private static final int LOADMORE = 0x02;
    private View loadMoreView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                case REFRESH:
//                    data.add(0, "刷新得到的数据");
//                    adapter.notifyDataSetChanged();
//                    mSwipeRefreshLayout.setRefreshing(false);
//                    break;
                case LOADMORE:
//                    for (int x = 0; x < 5; x++) {
//                        data.add(data.size(), "aaaaaa" + x);
//                    }
                    mHotUserListAdapter.notifyDataSetChanged();    //数据集变化后,通知adapter
                    int position = msg.arg1;
                    mListUserRoom.setSelection(position);    //设置选中项
                    loadMoreView.setVisibility(View.GONE);
                    break;
            }
        }
    };


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

}
