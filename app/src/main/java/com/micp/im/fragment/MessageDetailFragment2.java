package com.micp.im.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.adapter.MyChatRoomAdapter;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.HttpCallback;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.bean.UserBean;
import com.micp.im.event.ChatExitEvent;
import com.micp.im.ui.HomePageActivity;
import com.micp.im.utils.DpOrSp2PxUtil;
import com.micp.im.widget.BlackEditText;
import com.tuanmai.tools.toast.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MessageDetailFragment2 extends DialogFragment implements View.OnClickListener {

    TextView mTitle;
    BlackEditText mMessageInput;
    private UserBean mToUser;
    private RecyclerView mRecyclerView;
    private MyChatRoomAdapter mAdapter;
    private UserBean mUser;
    private BroadcastReceiver broadCastReceiver;
    private long lastTime = 0;
    private EMChatManager mChatManager;
    private List<EMMessage> mList;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private View mRootView;
    private Context mContext;
    private int mType;//0 个人主页进入的聊天页面 1是直播间进入的聊天页面


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.BottomViewTheme_Transparent);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_chat_detail, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpOrSp2PxUtil.dp2pxConvertInt(mContext, 300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onBack();
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);
        return mRootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }


    public void initView() {
        mTitle = (TextView) mRootView.findViewById(R.id.chat_user_name);
        mMessageInput = (BlackEditText) mRootView.findViewById(R.id.et_private_chat_message);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recylcerView);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressbar);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        mRootView.findViewById(R.id.iv_private_chat_send).setOnClickListener(this);
        mRootView.findViewById(R.id.et_private_chat_message).setOnClickListener(this);
        mRootView.findViewById(R.id.iv_private_chat_back).setOnClickListener(this);
        mChatManager = EMClient.getInstance().chatManager();
        initBroadCast();
    }

    public void initData() {
        mUser = AppContext.getInstance().getLoginUser();
        Bundle bundle = getArguments();
        mToUser = bundle.getParcelable("user");
        mType = bundle.getInt("type");
        mTitle.setText(mToUser.user_nicename);
        final EMConversation conversation = mChatManager.getConversation(mToUser.id);
        if (conversation != null) {
            Observable.create(new Observable.OnSubscribe<List<EMMessage>>() {

                @Override
                public void call(Subscriber<? super List<EMMessage>> subscriber) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    List<EMMessage> all = null;
                    List<EMMessage> currentList = conversation.getAllMessages();
                    int size = currentList.size();
                    if (currentList.size() < 20) {
                        all = conversation.loadMoreMsgFromDB(currentList.get(0).getMsgId(), 20 - size);
                        all.addAll(currentList);
                    } else {
                        all = currentList.subList(size - 20, size);
                    }
                    subscriber.onNext(all);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<EMMessage>>() {
                        @Override
                        public void call(List<EMMessage> emMessages) {
                            mList = emMessages;
                            mAdapter = new MyChatRoomAdapter(mContext, mList, mToUser, mUser);
                            mRecyclerView.setAdapter(mAdapter);
                            mLinearLayoutManager.scrollToPositionWithOffset(mList.size() - 1, 0);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发送私信
            case R.id.iv_private_chat_send:
                sendPrivateChat();
                break;
            case R.id.et_private_chat_message:

                break;
            case R.id.iv_private_chat_back:
                if (mType == 0) {
                    getActivity().finish();
                } else {
                    dismiss();
                }
                break;
            case R.id.btn_user:
                openUserPage();
                break;
        }
    }

    private void openUserPage() {
        Intent intent = new Intent(getActivity(), HomePageActivity.class);
        intent.putExtra("fromChat", 1);
        intent.putExtra("uid", mToUser.id);
        startActivity(intent);
    }

    //发送私信
    private void sendPrivateChat() {
        //判断是否操作频繁
        if ((System.currentTimeMillis() - lastTime) < 1000 && lastTime != 0) {
            AppContext.toast("操作频繁");
            return;
        }
        lastTime = System.currentTimeMillis();
        String content = mMessageInput.getText().toString();
        if ("".equals(content)) {
            AppContext.toast("内容为空");
            return;
        }
        checkIsBlacked(content);
    }

    //判断自己有没有被对方拉黑
    private void checkIsBlacked(final String content) {
        PhoneLiveApi.checkBlack(mToUser.id, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, JSONArray info) throws JSONException {
                JSONObject obj = info.getJSONObject(0);
                if (1 == obj.getInt("t2u")) {
                    AppContext.toast("你已经被对方拉黑");
                } else {
                    //把发送的消息传到后台
//                    String uid = AppContext.getInstance().getLoginUid();
//                    String token = AppContext.getInstance().getToken();

                    PhoneLiveApi.getSendMessage(AppContext.getInstance().getLoginUid(),
                            AppContext.getInstance().getToken(), mToUser.id,content,stringCallback);


                    //第三方通信
                    EMMessage message = EMMessage.createTxtSendMessage(content, mToUser.id);
                    mChatManager.sendMessage(message);
                    mMessageInput.setText("");
                    insertItem(message);

                }
            }
        });
    }



    private StringCallback stringCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int i) {

        }

        @Override
        public void onResponse(String s, int i) {
            JSONArray res = ApiUtils.checkIsSuccess(s);
            if(res!=null){

            }
        }
    };


    private void insertItem(EMMessage message) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(message);
        int position = mList.size() - 1;
        if (mAdapter == null) {
            mAdapter = new MyChatRoomAdapter(mContext, mList, mToUser, mUser);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyItemInserted(position);
        }
        mLinearLayoutManager.scrollToPositionWithOffset(position, 0);
    }


    //注册监听私信消息广播
    private void initBroadCast() {
        IntentFilter cmdFilter = new IntentFilter("com.duomizhibo.phonelive");
        if (broadCastReceiver == null) {
            broadCastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final EMMessage emMessage = intent.getParcelableExtra("cmd_value");
                    if(mToUser.id.equals(emMessage.getFrom())){
                        insertItem(emMessage);
                    }
                }
            };
        }
        getActivity().registerReceiver(broadCastReceiver, cmdFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadCastReceiver);
    }


    @Override
    public void onPause() {
        super.onPause();
        onBack();
    }

    private void onBack() {
        EMConversation conversation = mChatManager.getConversation(mToUser.id);
        if (conversation != null) {
            conversation.markAllMessagesAsRead();
        }
        if (mList != null && mList.size() > 0) {
            EMMessage emMessage = mList.get(mList.size() - 1);
            String toUserId = mToUser.id;
            String lastMsg = ((EMTextMessageBody) emMessage.getBody()).getMessage();
            ChatExitEvent e = new ChatExitEvent(lastMsg, toUserId, mType);
            MessageFragment.notifyComeback(e);
        }
    }

}
