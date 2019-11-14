package com.tuanmai.tools.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * ViewPager适配器
 */
public class ViewPagerAdapter extends PagerAdapter {

	private List<View> mListViews;
	private float wdithF=1f;

	public ViewPagerAdapter(List<View> mListViews) {
		this.mListViews = mListViews;
	}

    public ViewPagerAdapter(List<View> mListViews,float wdithF) {
        this.mListViews = mListViews;
        this.wdithF=wdithF;
    }

	// PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		//((ViewPager) arg0).removeView(mListViews.get(arg1));
        /**
         * 解决重叠问题
         */
        ((ViewPager) arg0).removeView((View) arg2);
	}

	// 获取要滑动的控件的数量
	@Override
	public int getCount() {
		return mListViews == null ? 0 : mListViews.size();
	}

	// 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		if ((mListViews.get(arg1)).getParent() != null) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}
		((ViewPager) arg0).addView(mListViews.get(arg1), 0);
		return mListViews.get(arg1);
	}

	// 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}



    /**
     * 页面宽度所占ViewPager测量宽度的权重比例，默认为1
     */
    @Override
    public float getPageWidth(int position) {
        return wdithF;
    }

}
