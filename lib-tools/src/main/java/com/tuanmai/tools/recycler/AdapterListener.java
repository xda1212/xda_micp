package com.tuanmai.tools.recycler;

import android.view.View;


/**
 * Created by LiuQiCong
 *
 * @date 2017-05-12 11:35
 * version 1.0
 * dsc RecyclerAdapter的回调
 */
public interface AdapterListener {

    void setItemView(MyViewHolder holder, int position, int viewType);
    void adapterListener(int actionType, int position, View view, Object object);
}
