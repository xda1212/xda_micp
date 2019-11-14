package com.micp.im.widget.list.pullrefresh.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.micp.im.R;
import com.micp.im.widget.list.pullrefresh.PullToRefreshBase.Mode;
import com.micp.im.widget.list.pullrefresh.PullToRefreshBase.Orientation;

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
