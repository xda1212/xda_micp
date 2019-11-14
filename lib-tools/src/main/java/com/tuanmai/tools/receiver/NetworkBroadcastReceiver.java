package com.tuanmai.tools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkBroadcastReceiver extends BroadcastReceiver {

	private OnNetworkChangeListener mListener;
	private ConnectivityManager connectivityManager;
	private NetworkInfo info;

	public interface OnNetworkChangeListener {
		void networkChange(boolean isNetwork);
	}

	public void setOnNetworkChangeListener(OnNetworkChangeListener listener) {
		mListener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			info = connectivityManager.getActiveNetworkInfo();
			boolean isNetwork;
            // 连接状态
            isNetwork = info != null && info.isAvailable();
			if (mListener != null) {
				mListener.networkChange(isNetwork);
			}
		}
	}

}
