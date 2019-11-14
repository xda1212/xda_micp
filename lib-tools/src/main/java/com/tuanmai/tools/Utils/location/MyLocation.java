package com.tuanmai.tools.Utils.location;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;


public final class MyLocation implements AMapLocationListener {

	public static final int STATUS_OK = 1;
	public static final int STATUS_ERROR = 0;

	private AMapLocationClient mLocationManagerProxy;
	private boolean isLoading = false;
	private OnLocationResultListener mOnLocationResultListener = null;

	public MyLocation( OnLocationResultListener listener) {
		mOnLocationResultListener = listener;
	}

	public void startRequestLocation(Context context) {
		isLoading = true;
		mLocationManagerProxy = new AMapLocationClient(context);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次,
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy.setLocationListener(this);
		AMapLocationClientOption option = new AMapLocationClientOption();
		option.setInterval(60 * 1000);
		option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		mLocationManagerProxy.setLocationOption(option);
		mLocationManagerProxy.startLocation();
	}

	public void stopRequestLocation() {
		if (isLoading) {
			// 移除定位请求
			mLocationManagerProxy.unRegisterLocationListener(this);
			// 销毁定位
			mLocationManagerProxy.onDestroy();
			isLoading = false;
		}
	}

	public boolean isFinished() {
		return !isLoading;
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getErrorCode() == 0) {
			// 定位成功回调信息，设置相关消息
			double lat = amapLocation.getLatitude();
			double lng = amapLocation.getLongitude();
			String address = amapLocation.getAddress();
			String provinceName = amapLocation.getProvince();
			String cityName = amapLocation.getCity();
			String county = amapLocation.getDistrict();

			String cityCode = amapLocation.getCityCode();
			String areaCode = amapLocation.getAdCode();
			mOnLocationResultListener.onLocationResult(STATUS_OK, amapLocation);
			stopRequestLocation();
		} else {
			mOnLocationResultListener.onLocationResult(STATUS_ERROR,
					amapLocation);
		}
	}

	public interface OnLocationResultListener {
		void onLocationResult(int status, AMapLocation amapLocation);

	}
}
