package com.tuanmai.tools.wheel;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public interface WheelViewAdapter {
	int getItemsCount();
	
	View getItem(int index, View convertView, ViewGroup parent);

	View getEmptyItem(View convertView, ViewGroup parent);
	
	void registerDataSetObserver(DataSetObserver observer);
	
	void unregisterDataSetObserver (DataSetObserver observer);
	
	void refreshItem(LinearLayout itemsLayout);
}
