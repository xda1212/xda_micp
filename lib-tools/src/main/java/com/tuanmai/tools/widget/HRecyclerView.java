package com.tuanmai.tools.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.tuanmai.tools.recycler.AdapterListener;
import com.tuanmai.tools.recycler.MyViewHolder;
import com.tuanmai.tools.recycler.RefreshLoadRecyclerAdapter;

import java.util.List;

/**
 * Created by LiuQiCong
 * date 2017-11-13 14:17
 * version 1.0
 * dsc 横向滚动
 */
public final class HRecyclerView extends RecyclerView {

    private RefreshLoadRecyclerAdapter adapter;
    private int padding = 20;

    public HRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public HRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        HLinearLayoutManager manager = new HLinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(manager);
        addItemDecoration(new ItemDecoration() {
            /*@Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(left,top,right,bottom);
            }*/

            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                super.getItemOffsets(outRect, itemPosition, parent);
                if (itemPosition + 1 == adapter.getDataSzie()) {
                    outRect.set(padding, 0, padding, 0);
                } else {
                    outRect.set(padding, 0, 0, 0);
                }
            }
        });
    }

    public void setRect(int padding) {
        this.padding = padding;
    }


    public void setDataList(@LayoutRes int layoutID, final List list, final HRecyclerListener listener) {
        adapter = new RefreshLoadRecyclerAdapter(getContext(), layoutID);
        adapter.setAdapterListener(new AdapterListener() {
            @Override
            public void setItemView(MyViewHolder holder, int position, int viewType) {
                if (null != listener) {
                    listener.setHItemView(holder, position);
                }
            }

            @Override
            public void adapterListener(int actionType, int position, View view, Object object) {

            }
        }, false, false, false);
        setAdapter(adapter);
        if (null != list && list.size() > 0) {
            adapter.addAll(list);
        }
    }

    public void setMoreView(View view, ViewGroup.LayoutParams layoutParams) {
        if (null != adapter) {
            adapter.setFooterView(view, layoutParams);
        }
    }


    public interface HRecyclerListener {
        void setHItemView(MyViewHolder holder, int position);
    }

    private final class HLinearLayoutManager extends LinearLayoutManager {

        public HLinearLayoutManager(Context context) {
            super(context);
        }

        public HLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public HLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                //try catch一下
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

}
