package com.micp.im.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.micp.im.adapter.LiveRecordAdapter;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.bean.LiveRecordBean;
import com.google.gson.Gson;
import com.micp.im.R;
import com.micp.im.ui.customviews.ActivityTitle;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 直播记录
 */
public class LiveRecordActivity extends ToolBarBaseActivity {
    @InjectView(R.id.lv_live_record)
    ListView mLiveRecordList;

    @InjectView(R.id.fensi)
    LinearLayout mFensi;

    @InjectView(R.id.load)
    LinearLayout mLoad;

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;


    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;
    private LiveRecordAdapter mLiveRecordAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_record;
    }

    @Override
    public void initView() {
        mLiveRecordList.setDividerHeight(1);
        mLiveRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLiveRecordBean = mLiveRecordAdapter.getItem(i);
                showLiveRecord();
            }
        });

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.liverecord));
        requestData();
    }

    //打开回放记录
    private void showLiveRecord() {

        showWaitDialog("正在获取回放...", false);
        PhoneLiveApi.getLiveRecordById(mLiveRecordBean.getId(), showLiveByIdCallback);

    }

    private StringCallback showLiveByIdCallback = new StringCallback() {
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
                    VideoBackActivity.startVideoBack(LiveRecordActivity.this, mLiveRecordBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    private StringCallback requestLiveRecordDataCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            mLoad.setVisibility(View.VISIBLE);
        }

        @Override
        public void onResponse(String response, int id) {
            JSONArray liveRecordJsonArray = ApiUtils.checkIsSuccess(response);

            if (null != liveRecordJsonArray) {
                String s = liveRecordJsonArray.toString();
                List<LiveRecordBean> list = new Gson().fromJson(s, new TypeToken<List<LiveRecordBean>>() {
                }.getType());
                if (list.size() > 0) {
                    if (mLiveRecordAdapter == null) {
                        mLiveRecordAdapter = new LiveRecordAdapter(list);
                        mLiveRecordList.setAdapter(mLiveRecordAdapter);
                    } else {
                        mLiveRecordAdapter.setList(list);
                    }
                } else {
                    mFensi.setVisibility(View.VISIBLE);
                }
            } else {
                mFensi.setVisibility(View.VISIBLE);
            }
        }
    };


    //请求数据
    private void requestData() {

        PhoneLiveApi.getLiveRecord(getIntent().getStringExtra("uid"), requestLiveRecordDataCallback);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getLiveRecordById");
        OkHttpUtils.getInstance().cancelTag("getLiveRecord");
    }
}
