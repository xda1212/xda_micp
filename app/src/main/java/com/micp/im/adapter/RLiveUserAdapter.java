package com.micp.im.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.bean.LiveJson;
import com.micp.im.ui.ReadyStartLiveActivity;
import com.micp.im.utils.LiveUtils;
import com.micp.im.utils.SimpleUtils;
import com.micp.im.widget.AvatarView;
import com.micp.im.widget.BlackTextView;
import com.micp.im.widget.LoadUrlImageView;

import java.util.List;

public class RLiveUserAdapter extends RecyclerView.Adapter<RLiveUserAdapter.ViewHolder>{

    private List<LiveJson> mUserList;
    private LayoutInflater inflater;


    public RLiveUserAdapter(LayoutInflater inflater , List<LiveJson> mUserList){
        this.inflater = inflater;
        this.mUserList = mUserList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_user,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
//        ViewHolder viewHolder;
//            viewHolder = new ViewHolder();

//            view.setTag(viewHolder);

        LiveJson live = mUserList.get(position);
//        viewHolder = (LiveUserAdapter.ViewHolder) view.getTag();
        viewHolder.mUserNick.setText(live.user_nicename);
        viewHolder.mUserLocal.setText(live.city);
        viewHolder.mUserHead.setAvatarUrl(live.avatar_thumb);
        viewHolder.mUserNums.setText(live.nums);
        viewHolder.mAnchorLevel.setImageResource(LiveUtils.getAnchorLevelRes2(live.level_anchor));
        //用于平滑加载图片
        SimpleUtils.loadImageForView(AppContext.getInstance(), viewHolder.mUserPic, live.thumb, 0);
        if (live.type != null) {
            if (live.type.equals("0")) {
                viewHolder.mIvType.setText("普通直播");
            }
            if (live.type.equals("1")) {
                viewHolder.mIvType.setText("密码直播");
            }
            if (live.type.equals("2")) {
                viewHolder.mIvType.setText("付费直播");
            }
            if (live.type.equals("3")) {
                viewHolder.mIvType.setText("计时直播");
            }
            if (live.type.equals("6")) {
                viewHolder.mIvType.setText("VIP房间");
            }
        }
        if (!TextUtils.isEmpty(live.title)) {
            viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
            viewHolder.mRoomTitle.setText(live.title);
        } else {
            viewHolder.mRoomTitle.setVisibility(View.GONE);
            viewHolder.mRoomTitle.setText("");
        }


        if (onitemClick != null) {
            viewHolder.mUserPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //在TextView的地方进行监听点击事件，并且实现接口
                    onitemClick.onItemClick(position);
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public BlackTextView mUserNick, mUserLocal, mUserNums, mRoomTitle;
        public LoadUrlImageView mUserPic;
        public AvatarView mUserHead;
        public ImageView mAnchorLevel;
        public TextView mIvType;

        public ViewHolder(View itemView) {
            super(itemView);
            mUserNick = (BlackTextView) itemView.findViewById(R.id.tv_live_nick);
            mUserLocal = (BlackTextView) itemView.findViewById(R.id.tv_live_local);
            mUserNums = (BlackTextView) itemView.findViewById(R.id.tv_live_usernum);
            mUserHead = (AvatarView) itemView.findViewById(R.id.iv_live_user_head);
            mUserPic = (LoadUrlImageView) itemView.findViewById(R.id.iv_live_user_pic);
            mRoomTitle = (BlackTextView) itemView.findViewById(R.id.tv_hot_room_title);
            mAnchorLevel = (ImageView) itemView.findViewById(R.id.iv_live_user_anchorlevel);
            mIvType = itemView.findViewById(R.id.iv_live_hot_type);
        }
    }


    private OnitemClick onitemClick;

    //定义设置点击事件监听的方法
    public void setOnitemClickLintener (OnitemClick onitemClick) {
        this.onitemClick = onitemClick;
    }
    public interface OnitemClick{
        void onItemClick(int position);
    }

}
