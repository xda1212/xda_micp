package com.tuanmai.tools.widget.unscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;


/**
 * @类名称：NoScrollGridView
 * @类描述：不可滑动的GRIDVIEW,用于SCROLLLAYOUT
 * @创建人：spreadken
 * @创建时间：2014-3-3 上午11:20:48
 * @修改人：spreadken
 * @修改时间：2014-3-3 上午11:20:48
 * @修改备注：
 */
public final class UnScrollGridView extends GridView {

    public UnScrollGridView(Context context) {
        super(context);
    }

    public UnScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}