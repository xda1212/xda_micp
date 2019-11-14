package com.tuanmai.tools.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuanmai.tools.R;

/**
 * 自定义头部布局
 * 
 * @ClassName: HeaderLayout
 * @Description: Activity或者Fragment的头部标题栏
 * @author lyc
 */
public final class HeaderLayout extends LinearLayout {

	private LayoutInflater mInflater;
	private View mHeader;
	public LinearLayout mLayoutLeftContainer, mLayoutRightContainer;
	public TextView mTvTitle;
	public ImageButton mLeftImageButton;
	public TextView mRightTextView;
	public RelativeLayout bgRelativeLayout;
	public ImageView mRightImageView;

	public HeaderLayout(Context context) {
		super(context);
		init(context);
	}

	public HeaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mInflater = LayoutInflater.from(context);
		mHeader = mInflater.inflate(R.layout.tools_header, null);
		addView(mHeader);
		initViews();
	}

	/** 初始化布局 */
	private void initViews() {
		mLayoutLeftContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_leftview_container);
		mLayoutRightContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_rightview_container);
		mTvTitle = (TextView) findViewByHeaderId(R.id.header_htv_subtitle);
		bgRelativeLayout = (RelativeLayout) findViewByHeaderId(R.id.header_layout_bg);

		initLeftImageButton();
		initRightTextView();

		mLayoutLeftContainer.setVisibility(View.INVISIBLE);
		mLayoutRightContainer.setVisibility(View.INVISIBLE);
		mTvTitle.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置背景图片
	 * 
	 * @param id
	 */
	public void setTitleBarBackgroundResource(int id) {
		if (bgRelativeLayout != null) {
			bgRelativeLayout.setBackgroundResource(id);
		}
	}
	public TextView getTitleTextView(){
		return mTvTitle;
	}
	/**
	 * 设置背景颜色
	 * 
	 * @param color
	 */
	public void setTitleBarBackgroundColor(int color) {
		if (bgRelativeLayout != null) {
			bgRelativeLayout.setBackgroundColor(color);
		}
	}

	/**
	 * 设置背景颜色
	 * 
	 * @param color
	 */
	public void setTitleTextColor(int color) {
		if (mTvTitle != null) {
			mTvTitle.setTextColor(color);
		}
	}

	/**
	 * 设置右侧字体颜色
	 * 
	 * @param color
	 */
	public void setRightTextColor(int color) {
		if (mRightTextView != null) {
			mRightTextView.setTextColor(color);
		}
	}

	/** 在TitleBar中查找指定控件 */
	public View findViewByHeaderId(int id) {
		return mHeader.findViewById(id);
	}

	/** 初始化左侧按钮 */
	private void initLeftImageButton() {
		View mleftImageButtonView = mInflater.inflate(
				R.layout.tools_header_leftbutton, mLayoutLeftContainer, true);
		mLeftImageButton = mleftImageButtonView.findViewById(R.id.ib_titlebar_left);
	}

	/** 初始化右侧按钮 */
	private void initRightTextView() {
		View mRightImageTextView = mInflater
				.inflate(R.layout.tools_header_rightbutton,
						mLayoutRightContainer, true);
		mRightTextView = mRightImageTextView.findViewById(R.id.ib_titlebar_right);
		mRightImageView = mRightImageTextView.findViewById(R.id.web_refresh_btn);
	}

	/** 设置TitleBar的标题 */
	public void setTitleBar(CharSequence title) {
        if(!TextUtils.isEmpty(title)){
            mTvTitle.setText(title);
            mTvTitle.setTextSize(16);
            mTvTitle.setVisibility(View.VISIBLE);
        }
	}

	/** 设置TitleBar的标题及左侧按钮背景 */
	public void setTitleBar(CharSequence title, int leftRes) {
		setTitleBar(title);
		mLeftImageButton.setImageResource(leftRes);
		mLayoutLeftContainer.setVisibility(View.VISIBLE);
	}

	/** 设置TitleBar的标题及右侧按钮图片 */
	public void setTitleBar(CharSequence title, Object object, int rightRes) {
		setTitleBar(title);
		mRightImageView.setImageResource(rightRes);
		mLayoutRightContainer.setVisibility(View.VISIBLE);
	}

	/** 设置TitleBar的标题及右侧按钮图片和文字 */
	public void setTitleBar(CharSequence title, Object object, int rightRes,
			CharSequence rightText) {
		setTitleBar(title, null, rightRes);
		mRightTextView.setText(rightText);
		mLayoutRightContainer.setVisibility(View.VISIBLE);
	}

	/** 设置TitleBar的标题及右侧按钮图片 */
	public void setTitleBar(CharSequence title, int leftRes, int rightRes) {
		setTitleBar(title, leftRes);
		mRightImageView.setImageResource(rightRes);
		mLayoutRightContainer.setVisibility(View.VISIBLE);
	}

	/** 设置TitleBar右侧按钮图片 */
	public void setRightView(int rightRes) {
		mRightTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(rightRes), null, null, null);
		mLayoutRightContainer.setVisibility(View.VISIBLE);
	}

	/** 设置TitleBar的标题及右侧按钮图片和文字 */
	public void setTitleBar(CharSequence title, int leftRes, int rightRes,
			CharSequence rightText) {
		setTitleBar(title, leftRes, rightRes);
		mRightTextView.setText(rightText);
		mLayoutRightContainer.setVisibility(View.VISIBLE);
	}

	/** 设置TitleBar的标题及右侧按钮文字 */
	public void setTitleBar(CharSequence title, Object object,
			CharSequence rightText) {
		setTitleBar(title);
		mRightTextView.setText(rightText);
		mLayoutRightContainer.setVisibility(View.VISIBLE);
	}

	/** 设置TitleBar的标题及右侧按钮文字 */
	public void setTitleBar(CharSequence title, int leftRes,CharSequence rightText) {
		setTitleBar(title, leftRes);
		mRightTextView.setText(rightText);
		mLayoutRightContainer.setVisibility(View.VISIBLE);
	}

	/** 设置左侧按钮点击事件 */
	public void setLeftListener(OnClickListener listener) {
		mLayoutLeftContainer.setOnClickListener(listener);
		mLeftImageButton.setOnClickListener(listener);
	}

	/** 设置右侧按钮点击事件 */
	public void setRightListener(OnClickListener listener) {
		mLayoutRightContainer.setOnClickListener(listener);
		mRightTextView.setOnClickListener(listener);
		mRightImageView.setOnClickListener(listener);
	}

	/** 自定义左侧控件 */
	public void setLeftView(View view) {
		mLayoutLeftContainer.removeAllViews();
		mLayoutLeftContainer.addView(view);
	}

	/** 自定义右侧控件 */
	public void setRightView(View view) {
		mLayoutRightContainer.removeAllViews();
		mLayoutRightContainer.addView(view);
	}

	/** 自定义右侧控件 */
	public void setRightView(View view, LinearLayout.LayoutParams params) {
		mLayoutRightContainer.removeAllViews();
		mLayoutRightContainer.addView(view, params);
	}
	public void setRightText(String text,int color){
		mRightTextView.setText(text);
		mRightTextView.setTextColor(color);
		mLayoutRightContainer.setVisibility(VISIBLE);
	}

    /**
     * 底部分割线
     * @return
     */
	public View getBottomLine(){
	    return findViewByHeaderId(R.id.header_layout_bottom_line);
    }
}
