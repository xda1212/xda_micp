package com.micp.im.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.adapter.MessageAdapter;
import com.micp.im.bean.PrivateChatUserBean;
import com.micp.im.bean.PrivateMessage;
import com.micp.im.bean.UserBean;
import com.micp.im.ui.other.PhoneLivePrivateChat;
import com.micp.im.widget.BlackEditText;
import com.micp.im.widget.BlackTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 *直播间私信发送页面
 */
public class MessageDetailDialogFragment extends DialogFragment{
    @InjectView(R.id.tv_private_chat_title)
    BlackTextView mTitle;

    @InjectView(R.id.et_private_chat_message)
    BlackEditText mMessageInput;

    private com.micp.im.interf.DialogInterface mDialogInterface;

    @InjectView(R.id.lv_message)
    ListView mChatMessageListView;


    private List<PrivateMessage> mChats = new ArrayList<>();
    private PrivateChatUserBean mToUser;
    private MessageAdapter mMessageAdapter;
    private UserBean mUser;

    private BroadcastReceiver broadCastReceiver;

    private long lastTime = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),R.style.BottomViewTheme_Transparent);
        dialog.setContentView(R.layout.dialog_fragment_private_chat_message);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        ButterKnife.inject(this,dialog);
        initData();
        initView(dialog);

        return dialog;
    }

    @OnClick({R.id.iv_private_chat_send,R.id.et_private_chat_message,R.id.iv_close})

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_private_chat_send:
//                PhoneLiveApi.checkBlack(mUser.id, mToUser.id, new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        JSONArray res = ApiUtils.checkIsSuccess(response);
//                        if (res != null) {
//                            try {
//                                String res1 = res.getJSONObject(0).getString("t2u");
//                                if (res1.equals("0")) {
//                                    sendPrivateChat();
//                                }else {
//                                    Toast.makeText(getContext(),"对方暂时拒绝接受您的消息",Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });

                break;
            case R.id.et_private_chat_message:

                break;
        }

    }
    //发送私信
    private void sendPrivateChat() {
        //判断是否操作频繁
        if((System.currentTimeMillis() - lastTime) < 1000 && lastTime != 0){
            Toast.makeText(getActivity(),"操作频繁",Toast.LENGTH_SHORT).show();
            return;
        }
        lastTime = System.currentTimeMillis();
        if(mMessageInput.getText().toString().equals("")){
            AppContext.showToastAppMsg(getActivity(),"内容为空");
            return;
        }
        if(mMessageInput.getText().toString().equals("")){
            AppContext.showToastAppMsg(getActivity(),"内容为空");
        }
        EMMessage emMessage = PhoneLivePrivateChat.sendChatMessage(mMessageInput.getText().toString(),mToUser);

        //更新列表
        updateChatList(PrivateMessage.crateMessage(emMessage,mUser.avatar));
        mMessageInput.setText("");
    }
    public void initData() {

        mUser = AppContext.getInstance().getLoginUser();
        mToUser = (PrivateChatUserBean) getArguments().getParcelable("user");

        try{
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mToUser.id);
            //指定会话消息未读数清零
            conversation.markAllMessagesAsRead();
        }catch (Exception e){
            e.printStackTrace();
        }
        mTitle.setText(mToUser.user_nicename);

        //获取历史消息
        mChats = PhoneLivePrivateChat.getUnreadRecord(mUser,mToUser);

        //初始化adapter
        mMessageAdapter = new MessageAdapter(getActivity());
        mMessageAdapter.setChatList(mChats);
        mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mChats.size() - 1);

        initBroadCast();

    }


    //注册监听私信消息广播
    private void initBroadCast() {
        IntentFilter cmdFilter = new IntentFilter("com.duomizhibo.phonelive");
        if(broadCastReceiver == null){
            broadCastReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    final EMMessage emMessage = intent.getParcelableExtra("cmd_value");
                    //判断是否是当前回话的消息
                    if(emMessage.getFrom().trim().equals(String.valueOf(mToUser.id))) {

                        updateChatList(PrivateMessage.crateMessage(emMessage,mToUser.avatar));

                    }
                }
            };
        }
        getActivity().registerReceiver(broadCastReceiver,cmdFilter);
    }


    public void initView(Dialog view) {

    }
    private void updateChatList(PrivateMessage message){
        //更新聊天列表
        mMessageAdapter.addMessage(message);
        mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mMessageAdapter.getCount()-1);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(broadCastReceiver);
        }catch (Exception e){

        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mDialogInterface != null){
            mDialogInterface.cancelDialog(null,null);
        }

    }

    public void setDialogInterface(com.micp.im.interf.DialogInterface dialogInterface){
        mDialogInterface = dialogInterface;
    }
}
