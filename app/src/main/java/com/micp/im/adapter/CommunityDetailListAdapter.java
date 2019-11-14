package com.micp.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micp.im.R;
import com.micp.im.bean.CommunityCommentData;
import com.micp.im.utils.TextViewUtil;
import com.micp.im.widget.AvatarView;
import com.micp.im.widget.BlackTextView;
import com.tuanmai.tools.Utils.ScreenUtil;
import com.tuanmai.tools.Utils.TextUtil;

import java.util.List;

public class CommunityDetailListAdapter extends BaseAdapter {
    private List<CommunityCommentData> mCommentData;
    private Context mContext;

    public CommunityDetailListAdapter(Context context, List<CommunityCommentData> communityData) {
        mContext = context;
        this.mCommentData = communityData;
    }

    @Override
    public int getCount() {
        return mCommentData.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommunityDetailListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment_detail, null);
            viewHolder = new CommunityDetailListAdapter.ViewHolder();
            viewHolder.mRootLayout = convertView.findViewById(R.id.item_comment_root_layout);
            viewHolder.mUserNick = convertView.findViewById(R.id.item_comment_user_name);
            viewHolder.mUserHead = convertView.findViewById(R.id.item_comment_user_head);
            viewHolder.mSex = convertView.findViewById(R.id.item_comment_user_sex);
            viewHolder.mData = convertView.findViewById(R.id.item_comment_date);
            viewHolder.mContent = convertView.findViewById(R.id.item_comment_content);
            viewHolder.mContentMore = convertView.findViewById(R.id.item_comment_content_more);

            convertView.setTag(viewHolder);
        }
        final CommunityCommentData commentData = mCommentData.get(position);
        viewHolder = (CommunityDetailListAdapter.ViewHolder) convertView.getTag();
        if (null != commentData.user) {
            if (!TextUtils.isEmpty(commentData.user.getUser_nicename())) {
                viewHolder.mUserNick.setText(commentData.user.getUser_nicename());
            }
            viewHolder.mUserHead.setAvatarUrl(commentData.user.getAvatar_thumb());
            viewHolder.mSex.setImageDrawable(commentData.user.getSex().equals("1")
                    ? mContext.getResources().getDrawable(R.drawable.global_male, mContext.getTheme())
                    : mContext.getResources().getDrawable(R.drawable.global_female, mContext.getTheme()));
        }
        viewHolder.mData.setText(commentData.diff_time);
        viewHolder.mContent.setText(commentData.content);
        viewHolder.mContent.setMaxLines(CommunityListAdapter.MAX_LINE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.mRootLayout.getLayoutParams();
        int viewWidth = ScreenUtil.getScreenWidth() - layoutParams.leftMargin - layoutParams.rightMargin
                - viewHolder.mRootLayout.getPaddingLeft() - viewHolder.mRootLayout.getPaddingRight();
        int lineCount = TextViewUtil.getLineCount(viewHolder.mContent.getTextSize(), viewWidth, commentData.content);
        if (lineCount > CommunityListAdapter.MAX_LINE) {
            viewHolder.mContentMore.setVisibility(View.VISIBLE);
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.mContentMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    finalViewHolder.mContent.setMaxLines(Integer.MAX_VALUE);
                }
            });
        } else {
            viewHolder.mContentMore.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        View mRootLayout;
        BlackTextView mUserNick;
        AvatarView mUserHead;
        ImageView mSex;
        TextView mData;
        TextView mContent;
        View mContentMore;
    }
}
