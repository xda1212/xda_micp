package com.tuanmai.tools.recycler;

import android.support.annotation.LayoutRes;

/**
 * Created by LiuQiCong
 *
 * @date 2017-05-12 11:41
 * version 1.0
 * dsc RecyclerAdapter自定义选择Item
 */
public interface ItemSelector {
    @LayoutRes
    int getItemLayout(int position);
}
