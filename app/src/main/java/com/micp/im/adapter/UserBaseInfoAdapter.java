package com.micp.im.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.micp.im.bean.SimpleUserInfo;
import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.utils.LiveUtils;
import com.micp.im.utils.SimpleUtils;
import com.micp.im.utils.StringUtils;
import com.micp.im.widget.BlackTextView;
import com.micp.im.widget.CircleImageView;

import java.util.List;

/**
 * 关注粉丝列表
 */
public class UserBaseInfoAdapter extends BaseAdapter {
    private List<SimpleUserInfo> users;

    public UserBaseInfoAdapter(List<SimpleUserInfo> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(AppContext.getInstance(), R.layout.item_attention_fans, null);
            viewHolder = new ViewHolder();
            viewHolder.mUHead = (CircleImageView) convertView.findViewById(R.id.cv_userHead);
            viewHolder.mUSex = (ImageView) convertView.findViewById(R.id.tv_item_usex);
            viewHolder.mULevel = (ImageView) convertView.findViewById(R.id.tv_item_ulevel);
            viewHolder.mULAnchorevel = (ImageView) convertView.findViewById(R.id.tv_item_anchorlevel);
            viewHolder.mUNice = (BlackTextView) convertView.findViewById(R.id.tv_item_uname);
            viewHolder.mUSign = (BlackTextView) convertView.findViewById(R.id.tv_item_usign);
            viewHolder.mIsFollow = (ImageView) convertView.findViewById(R.id.iv_item_attention);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SimpleUserInfo u = users.get(position);


        SimpleUtils.loadImageForView(AppContext.getInstance(), viewHolder.mUHead, u.avatar, 0);

        if (u.id.equals(AppContext.getInstance().getLoginUid())) {
            viewHolder.mIsFollow.setVisibility(View.GONE);
        } else {
            viewHolder.mIsFollow.setVisibility(View.VISIBLE);
        }
        viewHolder.mUSex.setImageResource(LiveUtils.getSexRes(u.sex));
        viewHolder.mIsFollow.setImageResource(StringUtils.toInt(u.isattention) == 1 ? R.drawable.me_following : R.drawable.me_follow);
        viewHolder.mULevel.setImageResource(LiveUtils.getLevelRes(u.level));
        viewHolder.mULAnchorevel.setImageResource(LiveUtils.getAnchorLevelRes(u.level_anchor));
        viewHolder.mUNice.setText(u.user_nicename);
        viewHolder.mUSign.setText(u.signature);
        viewHolder.mIsFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(), u.id, AppContext.getInstance().getToken(), new PhoneLiveApi.AttentionCallback() {
                    @Override
                    public void callback(boolean isAttention) {
                        if (isAttention) {
                            u.isattention = "1";
                            ((ImageView) v.findViewById(R.id.iv_item_attention)).setImageResource(R.drawable.me_following);
                        } else {
                            u.isattention = "0";
                            ((ImageView) v.findViewById(R.id.iv_item_attention)).setImageResource(R.drawable.me_follow);
                        }
                    }
                });
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public CircleImageView mUHead;
        public ImageView mUSex, mULevel, mIsFollow, mULAnchorevel;
        public BlackTextView mUNice, mUSign;
    }
}
