package com.tuanmai.tools.widget.unscroll;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 禁止左右滑动的ViewPager
 */
public class UnScrollViewPager extends ViewPager {

	private boolean isCanScroll = false;

	public UnScrollViewPager(Context context) {
		super(context);
	}

	public UnScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (isCanScroll) {
			return super.onTouchEvent(arg0);
		} else {
			return false;
		}

	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		super.setCurrentItem(item);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		try {
			if (isCanScroll) {
				return super.onInterceptTouchEvent(arg0);
			} else {
				return false;
			}
		} catch (IllegalArgumentException e) {
			return false;
		}

	}


}
