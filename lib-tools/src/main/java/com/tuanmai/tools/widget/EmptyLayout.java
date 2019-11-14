package com.tuanmai.tools.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuanmai.tools.R;

/**
 * 空页面
 */
public final class EmptyLayout extends FrameLayout {

	private LayoutInflater mInflater;
	private RelativeLayout bgRelativeLayout;
	private TextView mTxtShow;
	private ImageView mImgShow;
	private TextView mBtnRefresh;
	private View mEmpty;

	public EmptyLayout(Context context) {
		super(context);
		init(context);
	}

	public EmptyLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mInflater = LayoutInflater.from(context);
		mEmpty = mInflater.inflate(R.layout.tools_empty, null);
		addView(mEmpty);
		initViews();
	}

	private void initViews() {
		bgRelativeLayout = (RelativeLayout) findViewByHeaderId(R.id.empty_layout_bg);
		mTxtShow = (TextView) findViewByHeaderId(R.id.empty_txt_show);
		mImgShow = (ImageView) findViewByHeaderId(R.id.empty_img_show);
		mBtnRefresh = (TextView) findViewByHeaderId(R.id.empty_btn);
	}

	private void showBtn() {
		if (mBtnRefresh != null) {
			mBtnRefresh.setVisibility(View.VISIBLE);
		}
	}

	private void hideBtn() {
		if (mBtnRefresh != null) {
			mBtnRefresh.setVisibility(View.GONE);
		}
	}

	/** 在控件中查找指定控件 */
	public View findViewByHeaderId(int id) {
		return mEmpty.findViewById(id);
	}

	/**
	 * 设置背景颜色
	 */
	public void setEmptyBackground(int bgRes) {
		if (bgRelativeLayout != null) {
			bgRelativeLayout.setBackgroundResource(bgRes);
		}
	}

	/**
	 * 设置背景颜色
	 */
	public void setShowTextColor(int color) {
		if (mTxtShow != null) {
			mTxtShow.setTextColor(color);
		}
	}

	/** 设置Empty显示文字 */
	public void setEmptyShowTxt(CharSequence showTxt) {
		if (mTxtShow != null) {
			mTxtShow.setText(showTxt);
			mTxtShow.setVisibility(View.VISIBLE);
		}
	}

	/** 设置Empty显示图片 */
	public void setEmptyShowImg(int imgRes) {
		if (mImgShow != null) {
			mImgShow.setImageResource(imgRes);
			mImgShow.setVisibility(View.VISIBLE);
		}

	}

	/** 设置EmptyLayout */
	public void setEmpty(CharSequence showTxt, boolean isShowRereshBtn) {
		setEmptyShowTxt(showTxt);
		if (isShowRereshBtn) {
			showBtn();
		} else {
			hideBtn();
		}
	}

	/** 设置EmptyLayout */
	public void setEmpty(CharSequence showTxt, int imgRes,boolean isShowRereshBtn) {
		setEmptyShowTxt(showTxt);
		setEmptyShowImg(imgRes);
		if (isShowRereshBtn) {
			showBtn();
		} else {
			hideBtn();
		}
	}

	/** 设置EmptyLayout */
	public void setEmpty(CharSequence showTxt, int imgRes, int bgRes,boolean isShowRereshBtn) {
		setEmptyShowTxt(showTxt);
		setEmptyShowImg(imgRes);
		//setEmptyBackground(bgRes);
		if (isShowRereshBtn) {
			showBtn();
		} else {
			hideBtn();
		}
	}

	/** 设置EmptyLayout 自定义左侧控件 */
	public void setEmpty(View view) {
		bgRelativeLayout.removeAllViews();
		bgRelativeLayout.addView(view);
	}

	/** 显示EmptyLayout */
	public void show() {
		bgRelativeLayout.setVisibility(View.VISIBLE);
	}

	/** 隐藏EmptyLayout */
	public void hide() {
		bgRelativeLayout.setVisibility(View.GONE);
	}

	/** 设置刷新按钮点击事件 */
	public void setRefreshBtnistener(OnClickListener listener) {
		mBtnRefresh.setOnClickListener(listener);
	}
}
