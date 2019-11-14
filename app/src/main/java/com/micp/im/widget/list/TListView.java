package com.micp.im.widget.list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TListView extends ListView implements OnScrollListener {

	private List<OnScrollListener> mOnScrollListeners;

	public TListView(Context context) {
		this(context, null);
	}

	public TListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		this.mOnScrollListeners = new ArrayList<>();
		super.setOnScrollListener(this);
		setOverScrollMode(2);

	}

	public void setOnScrollListener(OnScrollListener l) {
		this.mOnScrollListeners.add(l);
	}

	public void removeOnScrollListener(OnScrollListener l) {
		if (l != null) {
			this.mOnScrollListeners.remove(l);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		for (OnScrollListener l : this.mOnScrollListeners) {
			l.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		for (OnScrollListener l : this.mOnScrollListeners) {
			if (l != null) {
				l.onScrollStateChanged(view, scrollState);
			}
		}
	}



}
