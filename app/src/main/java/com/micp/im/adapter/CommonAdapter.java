package com.micp.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/16.
 *
 * @author 李凯翔
 * @version 1.3.1
 *          desc  万能适配器
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    public CommonAdapter(Context context, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = new ArrayList<>();
        this.mItemLayoutId = itemLayoutId;
    }

    public CommonAdapter(Context context, List<T> list, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = list;
        this.mItemLayoutId = itemLayoutId;
    }

    public void add(T item) {
        mDatas.add(item);
        notifyDataSetChanged();
    }

    public void cleanData() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    public void remove(T t) {
        mDatas.remove(t);
        notifyDataSetChanged();
    }

    public void setNewData(List<T> list) {
        mDatas.clear();
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = mInflater.inflate(mItemLayoutId, null);
        convert(new MyViewHolder(itemView), getItem(position), position);
        return itemView;
    }

    public abstract void convert(MyViewHolder viewHolder, T item, int position);
}
