package com.micp.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micp.im.R;
import com.micp.im.bean.RecommendUser;
import com.micp.im.utils.UIHelper;
import com.micp.im.widget.AvatarView;
import com.micp.im.widget.BlackTextView;

import java.util.List;

public class RecommendUserAdapter extends BaseAdapter {
    private List<RecommendUser> mUserList;
    private LayoutInflater inflater;
    private Context mContext;

    public RecommendUserAdapter(Context context, LayoutInflater inflater, List<RecommendUser> mUserList) {
        mContext = context;
        this.mUserList = mUserList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RecommendUserAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_recommend_user, null);
            viewHolder = new RecommendUserAdapter.ViewHolder();
            viewHolder.mUserNick = (BlackTextView) convertView.findViewById(R.id.tv_live_nick);
            viewHolder.mUserHead = (AvatarView) convertView.findViewById(R.id.iv_live_user_head);
            viewHolder.mAnchorLevel = (TextView) convertView.findViewById(R.id.iv_live_user_anchorlevel);
            viewHolder.mSex = (ImageView) convertView.findViewById(R.id.item_recommend_user_sex);
            viewHolder.mSign = (TextView) convertView.findViewById(R.id.item_recommend_user_sign);
            viewHolder.mOnline = (TextView) convertView.findViewById(R.id.item_recommend_user_online);
            viewHolder.mDistance = (TextView) convertView.findViewById(R.id.item_recommend_user_distance);
            convertView.setTag(viewHolder);
        }
        final RecommendUser user = mUserList.get(position);
        viewHolder = (RecommendUserAdapter.ViewHolder) convertView.getTag();
        viewHolder.mUserNick.setText(user.user_nicename);
        viewHolder.mUserHead.setAvatarUrl(user.avatar_thumb);
        viewHolder.mAnchorLevel.setText("M" + user.level_anchor);
        if (null != user.sex) {
            viewHolder.mSex.setImageDrawable(user.sex.equals("1")
                    ? mContext.getResources().getDrawable(R.drawable.global_male, mContext.getTheme())
                    : mContext.getResources().getDrawable(R.drawable.global_female, mContext.getTheme()));
        }
        viewHolder.mSign.setText(user.signature);

//        Date date = new Date();
//        long currentTime = date.getTime() / 1000;
//        if (null != user.expiretime) {
//            long expireTime = Long.parseLong(user.expiretime);
//            long lastTime = currentTime - expireTime;
//            if (lastTime <= 0) {
//                viewHolder.mOnline.setText("在线");
//                viewHolder.mOnline.setBackground(mContext.getResources().getDrawable(R.drawable.shape_user_level_bg));
//            } else if (lastTime < 60 * 60) {
//                viewHolder.mOnline.setText("刚刚");
//                viewHolder.mOnline.setBackground(null);
//            } else if (lastTime < 60 * 60 * 24) {
//                long value = lastTime / (60 * 60);
//                viewHolder.mOnline.setText(value + "小时前");
//                viewHolder.mOnline.setBackground(null);
//            } else if (lastTime < 60 * 60 * 24 * 7) {
//                long value = lastTime / (60 * 60 * 24);
//                viewHolder.mOnline.setText(value + "天前");
//                viewHolder.mOnline.setBackground(null);
//            } else {
//                viewHolder.mOnline.setText("7天前");
//                viewHolder.mOnline.setBackground(null);
//            }
//        } else {
//            viewHolder.mOnline.setText("7天前");
//            viewHolder.mOnline.setBackground(null);
//        }
        if (null != user.online_status) {
            if ("在线".equals(user.online_status)) {
                viewHolder.mOnline.setBackground(mContext.getResources().getDrawable(R.drawable.shape_user_level_bg));
            } else {
                viewHolder.mOnline.setBackground(null);
            }
            viewHolder.mOnline.setText(user.online_status);
        }

//        try {
//            DPoint currentLocation = new DPoint(Double.valueOf(AppContext.lat), Double.valueOf(AppContext.lng));
//            DPoint userLocation = new DPoint(Double.valueOf(user.lat), Double.valueOf(AppContext.lng));
//            float distance = CoordinateConverter.calculateLineDistance(currentLocation, userLocation);
//            if (distance < 1000) {
//                viewHolder.mDistance.setText(distance + "m");
//            } else {
//                if (user.city.equals(AppContext.city)) {
//                    viewHolder.mDistance.setText(String.format("%.2fkm", distance / 1000));
//                } else {
//                    viewHolder.mDistance.setText(user.city);
//                }
//            }
//        } catch (NumberFormatException e) {
//            viewHolder.mDistance.setText("");
//        }
        if (null != user.distance) {
            viewHolder.mDistance.setText(user.distance);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showHomePageActivity(mContext, user.id);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public BlackTextView mUserNick;
        public AvatarView mUserHead;
        public TextView mAnchorLevel;
        public ImageView mSex;
        public TextView mSign;
        public TextView mOnline;
        public TextView mDistance;
    }
}
