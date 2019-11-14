package com.tuanmai.tools.recycler;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


public final class RecyclerDivider extends RecyclerView.ItemDecoration {

	private Drawable mDivider;
    private int mDividerHeight;
    private DividerListener mListener;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecyclerDivider(Context context, int draID, int dividerheight) {
        init(context,draID,dividerheight);
    }

    public RecyclerDivider(Context context, int draID) {
        //1px
        init(context,draID, 1);
    }


    private void init(Context context,int draID, int dividerheight){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDivider = context.getResources().getDrawable(draID,null);
        }else{
            mDivider = context.getResources().getDrawable(draID);
        }
        mDividerHeight= dividerheight;
    }



    //=======================================================================================
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView recyclerView) {
        int spanCount = getSpanCount(recyclerView);
        int childCount = recyclerView.getAdapter().getItemCount();
        if (isLastRow(recyclerView, itemPosition, spanCount, childCount)){
            // 如果是最后一行，则不需要绘制底部
            if(null==mListener || !mListener.unDivider(itemPosition)) {
                outRect.set(0, 0, mDividerHeight, 0);
            }

        } else if (isLastColumn(recyclerView, itemPosition, spanCount, childCount)){
            // 如果是最后一列，则不需要绘制右边
            if(null==mListener || !mListener.unDivider(itemPosition)) {
                outRect.set(0, 0, 0, mDividerHeight);
            }

        } else {
            if(null==mListener || !mListener.unDivider(itemPosition)) {
                outRect.set(0, 0, mDividerHeight, mDividerHeight);
            }
        }
    }

    private int getSpanCount(RecyclerView recyclerView) {// 列数
        int spanCount = -1;
        LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private boolean isLastColumn(RecyclerView recyclerView, int pos, int spanCount, int childCount) {
        LayoutManager layoutManager = recyclerView.getLayoutManager();
        boolean hasHeader=false;
        if(recyclerView.getAdapter() instanceof RecyclerAdapter){
            RecyclerAdapter adapter= (RecyclerAdapter) recyclerView.getAdapter();
            if(adapter.getHeader()!=null){
                hasHeader=true;
            }
        }
        if (layoutManager instanceof GridLayoutManager) {
            // 如果是最后一列，则不需要绘制右边
            if(hasHeader){
                if (pos % spanCount == 0) return true;
            }else{
                if ((pos + 1) % spanCount == 0) return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0) return true;

            } else {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一列，则不需要绘制右边
                if (pos >= childCount) return true;
            }

        }else if(layoutManager instanceof LinearLayoutManager){
            return true;

        }
        return false;
    }

    private boolean isLastRow(RecyclerView recyclerView, int pos, int spanCount, int childCount) {
        LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            // 如果是最后一行，则不需要绘制底部
            if (pos >= childCount) return true;

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount) return true;
            } else{// StaggeredGridLayoutManager 且横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) return true;
            }
        }
        return false;
    }




    //====================================================================================
    @Override
    public void onDraw(Canvas c, RecyclerView recyclerView, RecyclerView.State state) {
        //super.onDraw(c, recyclerView, state);
        drawHorizontal(c, recyclerView);
        drawVertical(c, recyclerView);
    }

	public void drawHorizontal(Canvas c, RecyclerView recyclerView) {
        recyclerView.getAdapter();

		int childCount = recyclerView.getChildCount();
        int left,top,right,bottom;
        int index;
		for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            index=recyclerView.getChildAdapterPosition(child);
            if(null==mListener || !mListener.unDivider(index)){
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                left = child.getLeft() - params.leftMargin;
                top = child.getBottom() + params.bottomMargin;
                right = child.getRight() + params.rightMargin + mDividerHeight;
                bottom = top + mDividerHeight;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
		}
	}

	public void drawVertical(Canvas c, RecyclerView recyclerView) {
		int childCount = recyclerView.getChildCount();
        int left,top,right,bottom;
		for (int i = 0; i < childCount; i++) {
			View child = recyclerView.getChildAt(i);
			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            left = child.getRight() + params.rightMargin;
			top = child.getTop() - params.topMargin;
            right = left + mDividerHeight;
			bottom = child.getBottom() + params.bottomMargin;
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}



	//====================================================================================
    public interface DividerListener {
        boolean unDivider(int adapterPosition);
    }

    public void setDividerListener(DividerListener dividerListener){
        mListener=dividerListener;
    }

}
