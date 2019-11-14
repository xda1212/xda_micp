package com.tuanmai.tools.recycler;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.tuanmai.tools.widget.pullrefresh.recycler.FooterView;

import java.util.ArrayList;
import java.util.List;

public final class RefreshLoadRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final int TYPE_HEADER = -100;
    private static final int TYPE_FOOTER = TYPE_HEADER - 1;
    private static final int TYPE_EMPTY = TYPE_HEADER - 2;

    public static final int ACTION_ONEND = 1;
    public static final int ACTION_CLICK = 2;
    public static final int ACTION_PRESS = 3;

    private boolean loadEnable;
    private boolean clickEnable;
    private boolean pressEnable;

    private Context mContext;

    private List mList;
    private int mLayoutID;
    private LayoutInflater mInflater;
    private AdapterListener mListener;
    private ItemSelector mItemSelector;
    private HolderExtraData mHolderExtraData;

    private View mEmptyView;
    private View mHeaderView;
    private View mFooterView;

    private ViewGroup.LayoutParams footerLP;
    //上次onEnd回调时的数据大小
    private int lastSize;

    /**
     * item多种布局
     */
    public RefreshLoadRecyclerAdapter(Context context, ItemSelector itemSelector) {
        if (null == itemSelector) {
            throw new RuntimeException("ItemSelector is null");
        }
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mItemSelector = itemSelector;
        this.mList = new ArrayList();
    }

    /**
     * item单个布局
     */
    public RefreshLoadRecyclerAdapter(Context context, int layoutID) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutID = layoutID;
        this.mList = new ArrayList();
    }

    public void setAdapterListener(AdapterListener listener, boolean load, boolean click, boolean press) {
        this.mListener = listener;
        this.loadEnable = load;
        this.clickEnable = click;
        this.pressEnable = press;
        if (load) {
            setFooterView(new FooterView(mContext));
        }
    }


    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (emptyHeight <= 0) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    emptyHeight = recyclerView.getHeight();
                }
            });
        }

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == TYPE_HEADER || type == TYPE_FOOTER) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    private int emptyHeight;

    @Override
    public void onViewAttachedToWindow(MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        final View itemView = holder.itemView;
        final ViewGroup.LayoutParams lp = itemView.getLayoutParams();
        int type = holder.getItemViewType();
        if (type == TYPE_EMPTY) {
            if (emptyHeight > 0) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = emptyHeight;
                itemView.setLayoutParams(lp);
            }

        } else if (type == TYPE_HEADER) {
            if (null != lp && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
            }
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            itemView.setLayoutParams(lp);

        } else if (type == TYPE_FOOTER) {
            if (null == footerLP) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                lp.width = footerLP.width;
                lp.height = footerLP.height;
            }
            itemView.setLayoutParams(lp);
            lastSize = 0;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (null != mEmptyView) {
                return TYPE_EMPTY;
            } else if (null != mHeaderView) {
                return TYPE_HEADER;
            } else if (null != mFooterView && mList.size() == 1) {
                return TYPE_FOOTER;
            }
        } else if ((null != mFooterView && null == mHeaderView && position == mList.size() - 1)
                || (null != mFooterView && null != mHeaderView && position == mList.size())) {
            return TYPE_FOOTER;
        }
        if (null != mItemSelector) {
            if (null == mHeaderView) {
                return mItemSelector.getItemLayout(position);
            } else {
                return mItemSelector.getItemLayout(position - 1);
            }
        }
        return mLayoutID;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (null != mEmptyView && viewType == TYPE_EMPTY) {
            return new MyViewHolder(mEmptyView);

        } else if (null != mHeaderView && viewType == TYPE_HEADER) {
            return new MyViewHolder(mHeaderView);

        } else if (null != mFooterView && viewType == TYPE_FOOTER) {
            return new MyViewHolder(mFooterView);

        } else if (null != mItemSelector) {
            //多种item布局时viewtype就是item的布局id(两者相等)
            MyViewHolder holder = new MyViewHolder(mInflater.inflate(viewType, parent, false));
            if (null != mHolderExtraData) {
                holder.setObjectList(mHolderExtraData.getHolderExtraData());
            }
            return holder;
        } else {
            MyViewHolder holder = new MyViewHolder(mInflater.inflate(mLayoutID, parent, false));
            if (null != mHolderExtraData) {
                holder.setObjectList(mHolderExtraData.getHolderExtraData());
            }
            return holder;
        }
    }

    @Override
    public int getItemCount() {
        if (null != mEmptyView) return 1;
        int itemCount = mList.size();
        if (null != mHeaderView) {
            ++itemCount;
        }
        return itemCount;
    }

    public int getDataSzie() {
        int size = 0;
        if (mList != null) {
            size = mList.size();
            if (null != mFooterView) {
                --size;
            }
        }
        return size;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        /**
         * 数据的数量>item的数量
         *  && getDataSzie()>getItemCount()
         */
        if (viewType == TYPE_FOOTER && null != mListener && getDataSzie() > 1) {
            showWait();
            if (lastSize != getDataSzie()) {
                lastSize = getDataSzie();
                final int finalPosition = position;
                holder.itemView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.adapterListener(ACTION_ONEND, finalPosition, holder.itemView, null);
                    }
                }, 120);
            }
        }
        /**
         * 非item的viewtype都小于0
         */
        if (viewType < 0) return;

        if (null != mHeaderView) --position;
        if (null != mListener) {
            mListener.setItemView(holder, position, viewType);
            // 如果设置了回调，则设置点击事件
            setCallBack(holder, position);
        }
    }

    private void setCallBack(final MyViewHolder holder, final int position) {
        if (clickEnable) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.adapterListener(ACTION_CLICK, position, holder.itemView, null);
                }
            });
        }

        if (pressEnable) {
            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.adapterListener(ACTION_PRESS, position, holder.itemView, null);
                    return false;
                }
            });
        }
    }


    //========================================================================================
    public void add(Object object) {
        removeEmptyView();
        if (null == mFooterView) {
            mList.add(object);
            notifyItemInserted(mList.size());
        } else {
            int index = mList.size() - 1;
            mList.add(index, object);
            notifyItemRangeChanged(index, 1);
        }
    }

    public void addAll(List list) {
        if (null != list && list.size() > 0) {
            int size = list.size();
            for (int i = 0; i < size; ++i) {
                add(list.get(i));
            }
        }
    }

    public void remove(int index) {
        if (index >= 0 && index < mList.size()) {
            mList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void replaceData(int index, Object object) {
        try {
            if (index >= 0 && index < mList.size()) {
                mList.remove(index);
                mList.add(index, object);
                notifyItemRemoved(index);
            }
        } catch (Exception e) {
        }
    }

    public void insertdata(int index, Object object) {
        try {
            if (index >= 0 && index < mList.size()) {
                mList.add(index, object);
                notifyItemRemoved(index);
            }

        } catch (Exception e) {
        }
    }

    /**
     * 获取数据
     */
    public <T extends Object> T get(int index) {
        if (index >= 0 && index < mList.size()) {
            return (T) mList.get(index);
        }
        return null;
    }

    /**
     * 清除数据
     */
    public void clear() {
        mList.clear();
        if (null != mFooterView) {
            mList.add(null);
        }
        notifyDataSetChanged();
    }

    public int size() {
        return null == mList ? 0 : mList.size();
    }


    //=========================header and footer and emptyView================================
    public void setHeaderView(View headerView) {
        removeEmptyView();
        if (null == mHeaderView) {
            mHeaderView = headerView;
            notifyItemChanged(0);
        }
    }

    public View getHeader() {
        return mHeaderView;
    }

    public void setFooterView(View footerView) {
        removeEmptyView();
        if (null == mFooterView) {
            mFooterView = footerView;
            mList.add(null);
            notifyItemChanged(mList.size());
        }
    }

    public void setFooterView(View footerView, ViewGroup.LayoutParams layoutParams) {
        this.footerLP = layoutParams;
        setFooterView(footerView);
    }

    public View getFooter() {
        return mFooterView;
    }


    public void setEmptyView(View emptyView) {
        if (mEmptyView != null) {
            return;
        }
        mEmptyView = emptyView;
        if (mList.size() > 0) {
            mList.clear();
        }
        mList.add(null);
        notifyDataSetChanged();
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void removeEmptyView() {
        if (null != mEmptyView) {
            mEmptyView = null;
        }
    }

    //=================================默认的footerView相关操作=========================================
    public void showWait() {
        if (mFooterView instanceof FooterView) {
            ((FooterView) mFooterView).showWait();
        }
    }

    public void showFinish() {
        if (mFooterView instanceof FooterView) {
            ((FooterView) mFooterView).showFinish();
        }
    }

    public void showGetMoreFail() {
        if (mFooterView instanceof FooterView) {
            ((FooterView) mFooterView).showGetMoreFail();
        }
    }

    public void showGetMore() {
        if (mFooterView instanceof FooterView) {
            ((FooterView) mFooterView).showGetMore();
        }
    }

    public void hideFooter() {
        if (mFooterView instanceof FooterView) {
            ((FooterView) mFooterView).hideFooter();
        }
    }

    public void setFooterBackgroundColor(@ColorInt int color) {
        if (null != mFooterView) {
            mFooterView.setBackgroundColor(color);
        }
    }

    public interface HolderExtraData {
        List<Object> getHolderExtraData();
    }

    public void setmHolderExtraData(HolderExtraData mHolderExtraData) {
        this.mHolderExtraData = mHolderExtraData;
    }
}