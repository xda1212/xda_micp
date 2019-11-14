package com.tuanmai.tools.widget.viewpagerindicator;

public interface IconPagerAdapter {
   
	/**
	 * drawable for Id | index == 0则不显示icon
	 * @FileName IconPagerAdapter.java  
	 * @author Simon.xin
	 */
    int getIconResId(int index);

    // From PagerAdapter
    int getCount();
}
