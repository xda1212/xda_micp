package com.micp.im.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.micp.im.AppConfig;
import com.micp.im.R;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.api.remote.ResponseCallback;
import com.micp.im.bean.ActiveBean;
import com.micp.im.bean.CommunityData;
import com.micp.im.ui.CommentDetailActivity;
import com.micp.im.ui.ImageViewPagerActivity;
import com.micp.im.ui.SmallVideoPlayerActivity;
import com.micp.im.utils.TextViewUtil;
import com.micp.im.utils.UIHelper;
import com.micp.im.widget.AvatarView;
import com.micp.im.widget.BlackTextView;
import com.tuanmai.tools.Utils.ScreenUtil;
import com.tuanmai.tools.Utils.fresco.FrescoUtils;

import java.util.ArrayList;
import java.util.List;

public class HomePageListAdapter extends BaseAdapter {

    public static final int MAX_LINE = 3;

    private List<CommunityData> mCommunityData;
    private Context mContext;

    private final int mSpanCount = 3;
    private boolean mIsShowFunctionLayout;

    private CommunityListListener mListener;

    public HomePageListAdapter(Context context, List<CommunityData> communityData, boolean isShowFunctionLayout) {
        mContext = context;
        this.mCommunityData = communityData;
        mIsShowFunctionLayout = isShowFunctionLayout;
    }

    @Override
    public int getCount() {
        return mCommunityData.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommunityData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HomePageListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_page_list, null);
            viewHolder = new HomePageListAdapter.ViewHolder();
            viewHolder.mRootLayout = convertView.findViewById(R.id.item_community_layout);
//            viewHolder.mUserNick = convertView.findViewById(R.id.item_community_user_name);
//            viewHolder.mUserHead = convertView.findViewById(R.id.item_community_user_head);
//            viewHolder.mAnchorLevel = convertView.findViewById(R.id.item_community_level);
//            viewHolder.mSex = convertView.findViewById(R.id.item_community_user_sex);
            viewHolder.mDistance = convertView.findViewById(R.id.item_community_distance);
            viewHolder.mData = convertView.findViewById(R.id.item_community_date);
            viewHolder.mContent = convertView.findViewById(R.id.item_community_content);
            viewHolder.mContentMore = convertView.findViewById(R.id.item_community_content_more);
            viewHolder.mShareButton = convertView.findViewById(R.id.item_community_share);
            viewHolder.mLikeButton = convertView.findViewById(R.id.item_community_like);
            viewHolder.mCommentButton = convertView.findViewById(R.id.item_community_comment);
            convertView.findViewById(R.id.item_community_function_layout).setVisibility(mIsShowFunctionLayout ? View.VISIBLE : View.GONE);

            viewHolder.mImageListAdapter = new CommunityImageListAdapter(mContext);
            viewHolder.mImageList = convertView.findViewById(R.id.item_community_image_list);
            viewHolder.mImageList.addItemDecoration(new RecyclerView.ItemDecoration() {
                int padding = (ScreenUtil.getScreenWidth() - mSpanCount * ScreenUtil.dp2px(100) - 2 * ScreenUtil.dp2px(25)) / 2;

                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    if (parent.getChildAdapterPosition(view) % mSpanCount != 0) {
                        outRect.left = padding;
                    }
                    outRect.top = padding;
                }
            });
            viewHolder.mImageList.setAdapter(viewHolder.mImageListAdapter);

            convertView.setTag(viewHolder);
        }
        final CommunityData communityData = mCommunityData.get(position);
        viewHolder = (HomePageListAdapter.ViewHolder) convertView.getTag();
//        if (null != communityData.user) {
//            viewHolder.mUserNick.setText(communityData.user.getUser_nicename());
//            viewHolder.mUserNick.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    UIHelper.showHomePageActivity(mContext, communityData.user.getId());
//                }
//            });
//            viewHolder.mUserHead.setAvatarUrl(communityData.user.getAvatar_thumb());
//            viewHolder.mUserHead.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    UIHelper.showHomePageActivity(mContext, communityData.user.getId());
//                }
//            });
//            viewHolder.mAnchorLevel.setText("M" + communityData.user.getLevel_anchor());
//            viewHolder.mSex.setImageDrawable(communityData.user.getSex().equals("1")
//                    ? mContext.getResources().getDrawable(R.drawable.global_male, mContext.getTheme())
//                    : mContext.getResources().getDrawable(R.drawable.global_female, mContext.getTheme()));
//        }
        viewHolder.mData.setText(communityData.diff_time);
        if (TextUtils.isEmpty(communityData.content)) {
            viewHolder.mContent.setVisibility(View.GONE);
        } else {
            viewHolder.mContent.setVisibility(View.VISIBLE);
            viewHolder.mContent.setText(communityData.content);
            viewHolder.mContent.setMaxLines(MAX_LINE);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.mRootLayout.getLayoutParams();
            int viewWidth = ScreenUtil.getScreenWidth() - layoutParams.leftMargin - layoutParams.rightMargin
                    - viewHolder.mRootLayout.getPaddingLeft() - viewHolder.mRootLayout.getPaddingRight();
            int lineCount = TextViewUtil.getLineCount(viewHolder.mContent.getTextSize(), viewWidth, communityData.content);
            if (lineCount > MAX_LINE) {
                viewHolder.mContentMore.setVisibility(View.VISIBLE);
                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.mContentMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        finalViewHolder.mContent.setMaxLines(Integer.MAX_VALUE);
                    }
                });
            } else {
                viewHolder.mContentMore.setVisibility(View.GONE);
            }
        }
        viewHolder.mDistance.setText(communityData.distance);

        viewHolder.mImageListAdapter.imageUrlList.clear();
        if (null != communityData.parsed_images && communityData.parsed_images.size() > 1) {
            viewHolder.mImageListAdapter.imageUrlList.addAll(communityData.parsed_images);
            viewHolder.mImageListAdapter.activeBean = null;
            viewHolder.mImageList.setLayoutManager(new GridLayoutManager(mContext, mSpanCount) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
        } else if (null != communityData.parsed_images && communityData.parsed_images.size() == 1) {
            viewHolder.mImageListAdapter.imageUrlList.addAll(communityData.parsed_images);
            viewHolder.mImageListAdapter.activeBean = null;
            viewHolder.mImageList.setLayoutManager(new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
        } else if (null != communityData.video) {
            viewHolder.mImageListAdapter.imageUrlList.add(communityData.video.getThumb());
            viewHolder.mImageListAdapter.activeBean = communityData.video;
            viewHolder.mImageList.setLayoutManager(new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
        }

        viewHolder.mImageListAdapter.notifyDataSetChanged();
        viewHolder.mImageListAdapter.setCallback(new CallBack() {
            @Override
            public void onItemClickListener(int position) {
                UIHelper.showImageViewPagerActivity(mContext, (ArrayList<String>) communityData.parsed_images, position);
            }
        });

        viewHolder.mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    if (null != communityData.parsed_images && communityData.parsed_images.size() > 0) {
                        mListener.showShareDialog("", communityData.content, communityData.parsed_images.get(0), AppConfig.COMMUNITY_SHARE_URL + communityData.id);
                    } else if (null != communityData.video) {
                        mListener.showShareDialog("", communityData.content, communityData.video.getThumb(), AppConfig.COMMUNITY_SHARE_URL + communityData.id);
                    }
                }
            }
        });
        viewHolder.mLikeButton.setSelected(communityData.is_like);
        viewHolder.mLikeButton.setText(communityData.likes);
        viewHolder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TextView) {
                    final TextView temp = (TextView) view;
                    if (!temp.isSelected()) {
                        PhoneLiveApi.communityLike(communityData.id, new ResponseCallback<Integer>() {

                            @Override
                            public void onSuccess(Integer responseBean) {
                                CommunityData tempData = mCommunityData.get(position);
                                tempData.is_like = responseBean == 1 ? true : false;
                                int count = Integer.parseInt(tempData.likes);
                                if (tempData.is_like) {
                                    count++;
                                }
                                tempData.likes = String.valueOf(count);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(mContext, "点赞失败", Toast.LENGTH_SHORT);
                            }
                        });
                    } else {
                        PhoneLiveApi.communityUnLike(communityData.id, new ResponseCallback<Integer>() {
                            @Override
                            public void onSuccess(Integer responseBean) {
                                CommunityData tempData = mCommunityData.get(position);
                                tempData.is_like = responseBean == 1 ? true : false;
                                int count = Integer.parseInt(tempData.likes);
                                if (!tempData.is_like) {
                                    count--;
                                }
                                tempData.likes = String.valueOf(count);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(mContext, "取消点赞失败", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                }
            }
        });
        viewHolder.mCommentButton.setText(communityData.comments);

        viewHolder.mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoComentDetail(communityData);
            }
        });
        viewHolder.mImageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoComentDetail(communityData);
            }
        });
        return convertView;
    }

    private void gotoComentDetail(CommunityData communityData) {
        Intent intent = new Intent(mContext, CommentDetailActivity.class);
        intent.putExtra(CommentDetailActivity.EXTRA_COMMUNITY_ID, communityData.id);
        mContext.startActivity(intent);
    }


    private class ViewHolder {
        View mRootLayout;
        //        BlackTextView mUserNick;
//        AvatarView mUserHead;
//        TextView mAnchorLevel;
//        ImageView mSex;
        TextView mDistance;
        TextView mData;
        TextView mContent;
        TextView mContentMore;
        RecyclerView mImageList;
        CommunityImageListAdapter mImageListAdapter;
        View mShareButton;
        TextView mCommentButton;
        TextView mLikeButton;
    }

    private class CommunityImageListAdapter extends RecyclerView.Adapter<CommunityViewHolder> {
        public List<String> imageUrlList = new ArrayList<>();
        private Context mContext;
        private CallBack mCallback;
        private ActiveBean activeBean;

        public CommunityImageListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public CommunityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_community_image, parent, false);
            return new CommunityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommunityViewHolder holder, final int position) {
            FrescoUtils.setDraweeImg(holder.simpleDraweeView, imageUrlList.get(position), holder.simpleDraweeView.getMeasuredWidth(), holder.simpleDraweeView.getMeasuredHeight());
            if (imageUrlList.size() > 1) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.simpleDraweeView.getLayoutParams();
                layoutParams.width = ScreenUtil.dp2px(100);
                layoutParams.height = ScreenUtil.dp2px(100);
                holder.videoBackground.setVisibility(View.GONE);
                holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.onItemClickListener(position);
                    }
                });
            } else {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.simpleDraweeView.getLayoutParams();
                layoutParams.width = ScreenUtil.dp2px(175);
                layoutParams.height = ScreenUtil.dp2px(175);
                if (null != activeBean) {
                    holder.videoBackground.setVisibility(View.VISIBLE);
                    holder.videoBackground.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SmallVideoPlayerActivity.startSmallVideoPlayerActivity(mContext, activeBean);
                        }
                    });
                } else {
                    holder.videoBackground.setVisibility(View.GONE);
                    holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallback.onItemClickListener(position);
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return imageUrlList.size();
        }

        public void setCallback(CallBack callback) {
            this.mCallback = callback;
        }
    }

    private interface CallBack {
        void onItemClickListener(int position);
    }

    private class CommunityViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView simpleDraweeView;
        private ImageView videoBackground;

        public CommunityViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = itemView.findViewById(R.id.item_community_image_list_image);
            videoBackground = itemView.findViewById(R.id.item_community_image_list_video_bg);
        }
    }

    public void setListener(CommunityListListener listener) {
        this.mListener = listener;
    }

    public interface CommunityListListener {
        void showShareDialog(String title, String describe, String imageUrl, String shareUrl);
    }
}
