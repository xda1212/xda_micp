package com.micp.im.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.adapter.UserBaseInfoAdapter;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.BaseFragment;
import com.micp.im.bean.SimpleUserInfo;
import com.micp.im.utils.UIHelper;
import com.micp.im.widget.BlackEditText;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 用户搜索
 */
public class SearchFragment extends BaseFragment {
    @InjectView(R.id.et_search_input)
    BlackEditText mSearchKey;

    @InjectView(R.id.lv_search)
    ListView mLvSearch;


    private UserBaseInfoAdapter  mUserBaseInfoAdapter;

    private List<SimpleUserInfo> mUserList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_index,null);
        ButterKnife.inject(this,view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        mSearchKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH  ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    search();
                    return true;
                }
                return false;
            }
        });

        mLvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showHomePageActivity(getActivity(),mUserList.get(position).id);
            }
        });

        mLvSearch.setAdapter(mUserBaseInfoAdapter = new UserBaseInfoAdapter(mUserList));


        mLvSearch.setEmptyView(view.findViewById(R.id.iv_empty));
    }

    @Override
    public void initData() {

    }
    @OnClick({R.id.tv_search_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_search_btn:
                getActivity().finish();
                //search();
                break;

        }
    }

    //搜索
    private void search() {


        String screenKey = mSearchKey.getText().toString().trim();
        if(TextUtils.isEmpty(screenKey)){
            return;
        }
        showWaitDialog();

        PhoneLiveApi.search(screenKey,new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response,int id) {
                hideWaitDialog();
                JSONArray res = ApiUtils.checkIsSuccess(response);

                if(null != res){
                    mUserList.clear();
                    mUserList.addAll(ApiUtils.formatDataToList2(res, SimpleUserInfo.class));
                    fillUI();
                }
            }
        }, AppContext.getInstance().getLoginUid());

    }

    private void fillUI() {
        mUserBaseInfoAdapter.notifyDataSetChanged();
    }

}
