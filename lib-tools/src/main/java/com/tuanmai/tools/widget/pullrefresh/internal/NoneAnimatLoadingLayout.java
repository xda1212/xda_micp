package com.tuanmai.tools.widget.pullrefresh.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.tuanmai.tools.R;
import com.tuanmai.tools.widget.pullrefresh.PullToRefreshBase.Mode;
import com.tuanmai.tools.widget.pullrefresh.PullToRefreshBase.Orientation;

/**
 * none animation
 */
public class NoneAnimatLoadingLayout extends LoadingLayout {

	public NoneAnimatLoadingLayout(Context context, Mode mode,
			Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.mipmap.tools_default_none;
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {

	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {

	}

	@Override
	protected void pullToRefreshImpl() {

	}

	@Override
	protected void refreshingImpl() {

	}

	@Override
	protected void releaseToRefreshImpl() {

	}

	@Override
	protected void resetImpl() {

	}

}
