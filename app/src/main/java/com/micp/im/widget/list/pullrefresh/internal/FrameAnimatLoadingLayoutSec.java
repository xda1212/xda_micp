package com.micp.im.widget.list.pullrefresh.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.micp.im.R;
import com.micp.im.widget.list.pullrefresh.PullToRefreshBase.Mode;
import com.micp.im.widget.list.pullrefresh.PullToRefreshBase.Orientation;
import com.tuanmai.tools.Utils.ScreenUtil;

public class FrameAnimatLoadingLayoutSec extends LoadingLayout {

	private boolean flag;

	public FrameAnimatLoadingLayoutSec(Context context, Mode mode,
									Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);

		mHeaderImage.setVisibility(GONE);
		mHeaderBgImage.setVisibility(GONE);
		mHeaderProgress.setVisibility(GONE);
		mHeaderSubImage.setVisibility(GONE);
//		mHeaderSloganImage.setVisibility(VISIBLE);
		mHeaderSloganImage.setVisibility(INVISIBLE);

		mFootLeaveImg.setVisibility(GONE);
		mFootLeaveImg.setVisibility(GONE);

		mHeaderSloganImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
		mHeaderSloganImage.setImageResource(R.drawable.tools_pc_pulltorefresh_loading);
		mHeaderSloganImage.setBackground(null);

		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				if (!flag) {
					FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mHeaderSloganImage.getLayoutParams();
					if (params != null) {
						params.height = ScreenUtil.dp2px(72);
					}
					postInvalidate();
					flag = true;
				}
			}
		});
	}

	@Override
	protected int getDefaultDrawableResId() {
		return 0;
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {

	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {

	}

	@Override
	protected void pullToRefreshImpl() {
		switch (mMode) {
			case PULL_FROM_START:
				AnimationDrawable r = (AnimationDrawable) mHeaderSloganImage.getDrawable();
				r.start();
				break;
			default:
				break;
		}
	}

	@Override
	protected void refreshingImpl() {
	}

	@Override
	protected void releaseToRefreshImpl() {}

	@Override
	protected void resetImpl() {
		mHeaderSloganImage.clearAnimation();
	}
}
