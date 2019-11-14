package com.micp.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.bean.ActiveBean;
import com.micp.im.bean.UserInfo;
import com.micp.im.utils.SimpleUtils;

import java.util.ArrayList;
import java.util.List;

public class MineAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<UserInfo.ListBean> userInfos;
    private Context context;
    public MineAdapter(LayoutInflater inflater,List<UserInfo.ListBean> userInfos) {
        this.inflater = inflater;
        this.userInfos = userInfos;

    }

    @Override
    public int getCount() {
        return userInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mine, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = convertView.findViewById(R.id.mine_icon);
            viewHolder.tv_name = convertView.findViewById(R.id.mine_name);
            convertView.setTag(viewHolder);
        }


        viewHolder = (ViewHolder) convertView.getTag();
        UserInfo.ListBean list = userInfos.get(position);
        viewHolder.tv_name.setText(list.getName());
        SimpleUtils.loadImageForView(AppContext.getInstance(),
                viewHolder.iv_icon,list.getThumb(), 0);

        return convertView;
    }

    private class ViewHolder {
        private ImageView iv_icon;
        private TextView tv_name;
    }
}
