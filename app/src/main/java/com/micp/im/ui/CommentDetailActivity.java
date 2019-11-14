package com.micp.im.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.micp.im.R;
import com.micp.im.adapter.CommunityDetailListAdapter;
import com.micp.im.adapter.CommunityListAdapter;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.api.remote.ResponseCallback;
import com.micp.im.bean.CommunityCommentData;
import com.micp.im.bean.CommunityData;
import com.micp.im.utils.KeybordUtil;
import com.micp.im.widget.list.AutoLoadListView;
import com.micp.im.widget.list.PullToRefreshAutoLoadListView;
import com.micp.im.widget.list.pullrefresh.PullToRefreshBase;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class CommentDetailActivity extends AppCompatActivity implements PullToRefreshBase.OnRefreshListener2, AutoLoadListView.loadMoreListener {

    public static final String EXTRA_COMMUNITY_ID = "extra_community_id";

    private PullToRefreshAutoLoadListView mPullToRefreshList;
    private AutoLoadListView mListView;
    private View mHeaderLayout;
    private View mNoDataLayout;
    private EditText mCommentEditText;
    private View mSendButton;

    private CommunityDetailListAdapter mAdapter;
    private List<CommunityCommentData> mData = new ArrayList<>();

    private String mCommunituId;
    private int pageIndex = 1;

    public static boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        isExist = true;
        initView();
        initData();

        getCommentDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExist = false;
    }

    private void initView() {
        this.findViewById(R.id.comment_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPullToRefreshList = this.findViewById(R.id.community_detail_list);
        mPullToRefreshList.setScrollingWhileRefreshingEnabled(false);
        mPullToRefreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullToRefreshList.setOnRefreshListener(this);
        mListView = mPullToRefreshList.getRefreshableView();
        mListView.enableAutoLoadMore(this, this);
        mListView.setSelector(R.color.white);

        mHeaderLayout = LayoutInflater.from(this).inflate(R.layout.header_comment_list, null);
        mListView.addHeaderView(mHeaderLayout);
        mAdapter = new CommunityDetailListAdapter(this, mData);
        mListView.setAdapter(mAdapter);

        mNoDataLayout = this.findViewById(R.id.community_detail_no_data);
        mCommentEditText = this.findViewById(R.id.comment_detail_comment_edit);
        mSendButton = this.findViewById(R.id.comment_detail_send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mCommentEditText.getText().toString())) {
                    PhoneLiveApi.postCommunityComment(mCommunituId, mCommentEditText.getText().toString(), new ResponseCallback<String>() {
                        @Override
                        public void onSuccess(String responseBean) {
                            onPullDownToRefresh(null);
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(CommentDetailActivity.this, "添加评论失败", Toast.LENGTH_SHORT);
                        }
                    });
                    mCommentEditText.setText("");
                    KeybordUtil.hideSoftKeybord(mCommentEditText);
                }
            }
        });
    }

    private void initData() {
        mCommunituId = getIntent().getStringExtra(EXTRA_COMMUNITY_ID);
        if (null == mCommunituId) {
            finish();
        }
    }

    private void getCommentDetail() {
        PhoneLiveApi.getCommunityDetail(mCommunituId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                showEmptyView();
            }

            @Override
            public void onResponse(String response, int id) {
                mPullToRefreshList.onRefreshComplete();
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("200".equals(obj.getString("ret"))) {
                        JSONObject data = obj.getJSONObject("data");
                        if (0 == data.getInt("code")) {
                            JSONObject info0 = data.getJSONObject("info");
                            if (info0 != null) {
                                CommunityData communityData = new Gson().fromJson(info0.toString(), CommunityData.class);
                                if (null != communityData.video) {
                                    communityData.video.setUserinfo(communityData.user);
                                }
                                List<CommunityData> dataList = new ArrayList<>();
                                dataList.add(communityData);
                                ListView listView = mHeaderLayout.findViewById(R.id.header_comment_listview);
                                CommunityListAdapter commentAdapter = new CommunityListAdapter(CommentDetailActivity.this, dataList, false);
                                listView.setAdapter(commentAdapter);
                                updateCommentListData();

                                mNoDataLayout.setVisibility(View.GONE);
                                return;
                            }
                        }
                    }
                    showEmptyView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showEmptyView() {
        mNoDataLayout.setVisibility(View.VISIBLE);
        mData.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void updateCommentListData() {
        PhoneLiveApi.getCommunityCommentList(mCommunituId, pageIndex, callback);
    }

    private ResponseCallback<List<CommunityCommentData>> callback = new ResponseCallback<List<CommunityCommentData>>() {
        @Override
        public void onSuccess(List<CommunityCommentData> responseBean) {
            mPullToRefreshList.onRefreshComplete();
            if (responseBean.size() > 0) {
                mListView.updateHasMore(true);
            } else {
                onLoadMoreFinish();
            }
            if (pageIndex == 1) {
                mData.clear();
            }
            mData.addAll(responseBean);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(String message) {
            mPullToRefreshList.onRefreshComplete();
            if (pageIndex == 1) {
            } else {
                pageIndex--;
                mListView.updateHasMore(true);
            }
            Toast.makeText(CommentDetailActivity.this, "获取评论失败", Toast.LENGTH_SHORT);
        }
    };

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageIndex = 1;
        getCommentDetail();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        updateCommentListData();
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
}
