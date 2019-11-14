package com.micp.im.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.micp.im.AppContext;
import com.micp.im.base.PrivateChatPageBase;
import com.micp.im.utils.TLog;

//私信已关注会话列表
public class FollowPrivateChatFragment extends PrivateChatPageBase {

    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    protected void initCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

    @Override
    protected void onNewMessage(final EMMessage message) {
        //收到消息
        try {
            if((message.getIntAttribute("isfollow") != 1)) return;
            addMessage(message);

        } catch (HyphenateException e) {
            //没有传送是否关注标记
            TLog.log("关注[没有传送是否关注标记]");
            //未传递标记请求服务端判断
//            PhoneLiveApi.getPmUserInfo(message.getFrom(),AppContext.getInstance().getLoginUid(), new StringCallback() {
//                @Override
//                public void onError(Call call, Exception e,int id) {
//
//                }
//
//                @Override
//                public void onResponse(String response,int id) {
//                    JSONArray res = ApiUtils.checkIsSuccess(response);
//                    if(null != res){
//                        try {
//                            PrivateChatUserBean privateChatUserBean = new Gson().fromJson(res.getString(0),PrivateChatUserBean.class);
//                            if(privateChatUserBean.isattention2 == 1){
//                                addMessage(message);
//                            }
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//
//                    }
//
//                }
//            });
            e.printStackTrace();
        }

    }

    private void addMessage(EMMessage message){
        //判断是否在列表中,如果在更新最后一条信息,如果没在添加一条item
        if(!emConversationMap.containsKey(message.getFrom())){
            TLog.log("已关注[不存会话列表]");
            inConversationMapAddItem(message);

        }else{
            if(mPrivateChatListData == null)return;
            TLog.log("已关注[存在会话列表]");
            updataLastMessage(message);

        }
    }


    @Override
    public void initData() {
        mUser = AppContext.getInstance().getLoginUser();
        updatePrivateChatList();
        initConversationList(1);

    }





}
