package com.micp.im.widget.list.pullrefresh.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.micp.im.widget.list.pullrefresh.PullToRefreshBase.Mode;
import com.micp.im.widget.list.pullrefresh.PullToRefreshBase.Orientation;

public class FrameAnimatLoadingLayout extends LoadingLayout {

	private RotateAnimation rotateAnimation;
	private RotateAnimation rotateAnimationPlane;
	private boolean flag = false;

	public FrameAnimatLoadingLayout(Context context, Mode mode,
									   Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);

		rotateAnimation = new RotateAnimation(0, 360 * 5,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		rotateAnimation.setDuration(4000 * 5);
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		rotateAnimation.setRepeatMode(Animation.RESTART);

		rotateAnimationPlane = new RotateAnimation(0, -360 * 5,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimationPlane.setInterpolator(new LinearInterpolator());
		rotateAnimationPlane.setDuration(4000 * 5);
		rotateAnimationPlane.setRepeatCount(Animation.INFINITE);
		rotateAnimationPlane.setRepeatMode(Animation.RESTART);
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
				mHeaderImage.startAnimation(rotateAnimation);
				mHeaderSubImage.startAnimation(rotateAnimationPlane);
				break;

			case PULL_FROM_END:
				mFootImg.setVisibility(View.VISIBLE);
				mFootLeaveImg.setVisibility(View.GONE);
				AnimationDrawable animRefresh = (AnimationDrawable) mFootImg.getDrawable();
				animRefresh.start();
				break;

			default:break;
		}

	}

	@Override
	protected void refreshingImpl() {

	}

	@Override
	protected void releaseToRefreshImpl() {
		if (mMode == Mode.PULL_FROM_END){
			flag = true;
		}

	}

	@Override
	protected void resetImpl() {
		mHeaderImage.clearAnimation();
		mHeaderSubImage.clearAnimation();
		if (flag) {
			AnimationDrawable animRefresh = (AnimationDrawable) mFootImg.getDrawable();
			animRefresh.stop();
			mFootLeaveImg.setVisibility(View.VISIBLE);
			mFootImg.setVisibility(View.GONE);

			AnimationDrawable animRefreshLeave = (AnimationDrawable) mFootLeaveImg.getDrawable();
			animRefreshLeave.start();
			flag = false;
		}

	}

}
