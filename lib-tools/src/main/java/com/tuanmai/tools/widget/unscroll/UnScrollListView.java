package com.tuanmai.tools.widget.unscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 不可滑动的LISTVIEW
 */
public class UnScrollListView extends ListView {

	public UnScrollListView(Context context) {
		super(context);
	}

	public UnScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public UnScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, mExpandSpec);
	}
}
