package com.tuanmai.tools.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CusRelativeLayout extends RelativeLayout {

	public CusRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setChildrenDrawingOrderEnabled(true);
	}


    /**
     * 返回当前迭代子视图的索引.就是说 获取当前正在绘制的视图索引.  如果需要改变ViewGroup子视图绘制的顺序,则需要重载这个方法
     */
	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (i == 0)
			return 2;
		if (i == 1)
			return 3;
		if (i == 2)
			return 0;
		if (i == 3)
			return 1;
		return super.getChildDrawingOrder(childCount, i);
	}

}
