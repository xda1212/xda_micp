package com.tuanmai.tools.widget.pullrefresh.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tuanmai.tools.R;
import com.tuanmai.tools.widget.pullrefresh.ILoadingLayout;
import com.tuanmai.tools.widget.pullrefresh.PullToRefreshBase.Mode;
import com.tuanmai.tools.widget.pullrefresh.PullToRefreshBase.Orientation;

@SuppressLint("ViewConstructor")
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {

	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

	private FrameLayout mInnerLayout;

	protected final ImageView mHeaderBgImage;
	protected final ImageView mHeaderImage;
	protected final ImageView mHeaderSubImage;
	protected final ImageView mHeaderSloganImage;

	protected final ProgressBar mHeaderProgress;

	private boolean mUseIntrinsicAnimation;

	private final TextView mHeaderText;
	private final TextView mSubHeaderText;

	protected final Mode mMode;
	protected final Orientation mScrollDirection;

	private CharSequence mPullLabel;
	private CharSequence mRefreshingLabel;
	private CharSequence mReleaseLabel;

	private LinearLayout mHeaderTextLayout;

	protected ImageView mFootImg;
	protected ImageView mFootLeaveImg;

	public LoadingLayout(Context context, final Mode mode,
			final Orientation scrollDirection, TypedArray attrs) {
		super(context);

		mMode = mode;
		mScrollDirection = scrollDirection;
		switch (scrollDirection) {
            case HORIZONTAL:
                LayoutInflater.from(context).inflate(
                        R.layout.tools_refresh_header_horizontal, this);
                break;
            case VERTICAL:
            default:
                LayoutInflater.from(context).inflate(
                        R.layout.tools_refresh_header_vertical, this);
                break;
		}

		mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
		mHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);
		mHeaderProgress = (ProgressBar) mInnerLayout.findViewById(R.id.pull_to_refresh_progress);
		mSubHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);

		mHeaderBgImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_bg_layout);
		mHeaderImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_image);
		mHeaderSubImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_sub_image);
		mHeaderSloganImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_slogan_image);
		setTheme(context);

		mHeaderTextLayout = (LinearLayout) mInnerLayout.findViewById(R.id.pull_to_refresh_text_layout);
		mFootImg = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_upimg);
		mFootLeaveImg = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_finish);

		LayoutParams lp = (LayoutParams) mInnerLayout.getLayoutParams();
		switch (mode) {
		case PULL_FROM_END:
			lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.TOP:Gravity.LEFT;
			// Load in labels
			mPullLabel = context
					.getString(R.string.pull_to_refresh_from_bottom_pull_label);
			mRefreshingLabel = context
					.getString(R.string.pull_to_refresh_from_bottom_refreshing_label);
			mReleaseLabel = context
					.getString(R.string.pull_to_refresh_from_bottom_release_label);
			mInnerLayout.getChildAt(0).setVisibility(View.GONE);
			mInnerLayout.getChildAt(1).setVisibility(View.VISIBLE);
			break;

		case PULL_FROM_START:
		default:
			lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.BOTTOM:Gravity.RIGHT;
			// Load in labels
			mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
			mRefreshingLabel = context
					.getString(R.string.pull_to_refresh_refreshing_label);
			mReleaseLabel = context
					.getString(R.string.pull_to_refresh_release_label);
			mInnerLayout.getChildAt(0).setVisibility(View.VISIBLE);
			mInnerLayout.getChildAt(1).setVisibility(View.GONE);
			break;
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrShowHeaderText)) {
			boolean show = attrs.getBoolean(R.styleable.PullToRefresh_ptrShowHeaderText, false);
			if (!show) {
				mHeaderTextLayout.setVisibility(View.GONE);
			}
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
			Drawable background = attrs.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
			if (null != background) {
				ViewCompat.setBackground(this, background);
			}
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance,
					styleID);
			setTextAppearance(styleID.data);
		}
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(
					R.styleable.PullToRefresh_ptrSubHeaderTextAppearance,
					styleID);
			setSubTextAppearance(styleID.data);
		}

		// Text Color attrs need to be set after TextAppearance attrs
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextColor)) {
			ColorStateList colors = attrs
					.getColorStateList(R.styleable.PullToRefresh_ptrHeaderTextColor);
			if (null != colors) {
				setTextColor(colors);
			}
		}
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderSubTextColor)) {
			ColorStateList colors = attrs
					.getColorStateList(R.styleable.PullToRefresh_ptrHeaderSubTextColor);
			if (null != colors) {
				setSubTextColor(colors);
			}
		}

		// Try and get defined drawable from Attrs
		Drawable imageDrawable = null;

		if (mHeaderImage.getDrawable() != null) {
			imageDrawable = mHeaderImage.getDrawable();
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawable)) {
			imageDrawable = attrs
					.getDrawable(R.styleable.PullToRefresh_ptrDrawable);
		}

		// Check Specific Drawable from Attrs, these overrite the generic
		// drawable attr above
		switch (mode) {
		case PULL_FROM_START:
		default:
			if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableStart)) {
				imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableStart);
			} else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableTop)) {
				imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableTop);
			}
			break;

		case PULL_FROM_END:
			if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableEnd)) {
				imageDrawable = attrs
						.getDrawable(R.styleable.PullToRefresh_ptrDrawableEnd);
			} else if (attrs
					.hasValue(R.styleable.PullToRefresh_ptrDrawableBottom)) {
				imageDrawable = attrs
						.getDrawable(R.styleable.PullToRefresh_ptrDrawableBottom);
			}
			break;
		}

		// If we don't have a user defined drawable, load the default
		if (null == imageDrawable) {
			imageDrawable = context.getResources().getDrawable(R.mipmap.tools_refresh_earth);
		}
		// Set Drawable, and save width/height
		setLoadingDrawable(imageDrawable);
		reset();
	}

	/**
	 * 临时更换主题
	 */
	private void setTheme(Context context) {
		/*if (ConfigManager.getBmms(
				URL_KEY.THEME_RESOURCE_NAME).equalsIgnoreCase("Root")) {// 临时更换主题
		}*/
	}

	public final void setHeight(int height) {
		ViewGroup.LayoutParams lp = getLayoutParams();
		lp.height = height;
		requestLayout();
	}

	public final void setWidth(int width) {
		ViewGroup.LayoutParams lp = getLayoutParams();
		lp.width = width;
		requestLayout();
	}

	public final int getContentSize() {
		switch (mScrollDirection) {
		case HORIZONTAL:
			return mInnerLayout.getWidth();
		case VERTICAL:
		default:
			return mInnerLayout.getHeight();
		}
	}

	public final void hideAllViews() {
		if (View.VISIBLE == mHeaderText.getVisibility()) {
			mHeaderText.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mHeaderProgress.getVisibility()) {
			mHeaderProgress.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mHeaderImage.getVisibility()) {
			mHeaderImage.setVisibility(View.INVISIBLE);
		}

		if (View.VISIBLE == mHeaderSubImage.getVisibility()) {
			mHeaderSubImage.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mSubHeaderText.getVisibility()) {
			mSubHeaderText.setVisibility(View.INVISIBLE);
		}
	}

	public final void onPull(float scaleOfLayout) {
		if (!mUseIntrinsicAnimation) {
			onPullImpl(scaleOfLayout);
		}
	}

	public final void pullToRefresh() {
		if (null != mHeaderText) {
			mHeaderText.setText(mPullLabel);
		}

		// Now call the callback
		pullToRefreshImpl();
	}

	public final void refreshing() {
		if (null != mHeaderText) {
			mHeaderText.setText(mRefreshingLabel);
		}

		if (mUseIntrinsicAnimation) {
			((AnimationDrawable) mHeaderImage.getDrawable()).start();
		} else {
			// Now call the callback
			refreshingImpl();
		}

		if (null != mSubHeaderText) {
			mSubHeaderText.setVisibility(View.GONE);
		}
	}

	public final void releaseToRefresh() {
		if (null != mHeaderText) {
			mHeaderText.setText(mReleaseLabel);
		}

		// Now call the callback
		releaseToRefreshImpl();
	}

	public final void reset() {
		if (null != mHeaderText) {
			mHeaderText.setText(mPullLabel);
		}
		mHeaderImage.setVisibility(View.VISIBLE);
		mFootImg.setVisibility(View.VISIBLE);

		if (mUseIntrinsicAnimation) {
			((AnimationDrawable) mHeaderImage.getDrawable()).stop();
		} else {
			// Now call the callback
			resetImpl();
		}

		if (null != mSubHeaderText) {
			if (TextUtils.isEmpty(mSubHeaderText.getText())) {
				mSubHeaderText.setVisibility(View.GONE);
			} else {
				mSubHeaderText.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		setSubHeaderText(label);
	}

	public final void setLoadingDrawable(Drawable imageDrawable) {
		// Set Drawable
		mHeaderImage.setImageDrawable(imageDrawable);
		mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable);
		mFootImg.setImageDrawable(getResources().getDrawable(
				R.drawable.tools_upload_anim_refresh));
		mFootLeaveImg.setImageDrawable(getResources().getDrawable(
				R.drawable.tools_upload_anim_finish));

		// Now call the callback
		onLoadingDrawableSet(imageDrawable);
	}

	public void setPullLabel(CharSequence pullLabel) {
		mPullLabel = pullLabel;
	}

	public void setRefreshingLabel(CharSequence refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(CharSequence releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	@Override
	public void setTextTypeface(Typeface tf) {
		mHeaderText.setTypeface(tf);
	}

	public final void showInvisibleViews() {
		if (View.INVISIBLE == mHeaderText.getVisibility()) {
			mHeaderText.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mHeaderProgress.getVisibility()) {
			mHeaderProgress.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mHeaderImage.getVisibility()) {
			mHeaderImage.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mSubHeaderText.getVisibility()) {
			mSubHeaderText.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Callbacks for derivative Layouts
	 */

	protected abstract int getDefaultDrawableResId();

	protected abstract void onLoadingDrawableSet(Drawable imageDrawable);

	protected abstract void onPullImpl(float scaleOfLayout);

	protected abstract void pullToRefreshImpl();

	protected abstract void refreshingImpl();

	protected abstract void releaseToRefreshImpl();

	protected abstract void resetImpl();

	private void setSubHeaderText(CharSequence label) {
		if (null != mSubHeaderText) {
			if (TextUtils.isEmpty(label)) {
				mSubHeaderText.setVisibility(View.GONE);
			} else {
				mSubHeaderText.setText(label);
				// Only set it to Visible if we're GONE, otherwise VISIBLE will
				// be set soon
				if (View.GONE == mSubHeaderText.getVisibility()) {
					mSubHeaderText.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void setSubTextAppearance(int value) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	private void setSubTextColor(ColorStateList color) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

	private void setTextAppearance(int value) {
		if (null != mHeaderText) {
			mHeaderText.setTextAppearance(getContext(), value);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	private void setTextColor(ColorStateList color) {
		if (null != mHeaderText) {
			mHeaderText.setTextColor(color);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

}
