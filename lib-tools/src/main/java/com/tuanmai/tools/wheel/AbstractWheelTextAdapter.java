package com.tuanmai.tools.wheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuanmai.tools.R;


@SuppressLint("InflateParams")
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {

    private final int []TEXT_COLOR={0x332c3033,0x552c3033,0x772c3033,0xff2c3033,0x772c3033,0x552c3033,0x332c3033};
    private final int []TEXT_SIZE={14,16,17,20,17,16,14};
    // Current context
    protected Context mContext;

    protected AbstractWheelTextAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * Returns text for specified item
     * @param index the item index
     * @return the text of specified items
     */
    protected abstract CharSequence getItemText(int index);

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tools_item_selector, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.seletor_text);
        if (textView != null) {
            CharSequence text = getItemText(index);
            if (text == null) text = "";
            textView.setText(text);

            int position=index+3;
            position%=TEXT_COLOR.length;
            textView.setTextColor(TEXT_COLOR[position]);
            textView.setTextSize(TEXT_SIZE[position]);
        }
        return convertView;
    }

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext,R.layout.tools_item_selector, null);
        }
        return convertView;
    }

    public synchronized void refreshItem(LinearLayout itemsLayout){
        View itemView;
        TextView textView;
        int count=itemsLayout.getChildCount();
        for(int i=count-TEXT_COLOR.length;i<count;++i){
            itemView=itemsLayout.getChildAt(i);
            if(null!=itemView){
                textView= (TextView) itemView.findViewById(R.id.seletor_text);
                if(null!=textView){
                    int index=i%TEXT_COLOR.length;
                    textView.setTextColor(TEXT_COLOR[index]);
                    textView.setTextSize(TEXT_SIZE[index]);
                }
            }
        }
    }


}
