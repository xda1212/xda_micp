package com.tuanmai.tools.Utils.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.amap.api.location.AMapLocation;
import com.tuanmai.tools.Utils.location.MyLocation.OnLocationResultListener;

/**
 * 定位管理类(高德地图)
 */
public final class LocationManager {

    public static final int STATUS_OK = 1;
    public static final int STATUS_ERROR = 0;

    public ILocationListener callback;
    private MyLocation mMyLocation = null;

    public interface ILocationListener {
        void onLocationSuccess(LocationBean location);

        void onLocationFail();
    }

    private LocationManager() {
    }

    /**
     * 取得管理器
     *
     * @return
     * @author liyinchu
     * @date 2015-7-13下午5:33:21
     */
    public static LocationManager getInstance() {
        if (instance == null) {
            instance = new LocationManager();
        }
        return instance;
    }

    private static LocationManager instance = null;

    /**
     * 定位数据实体
     */
    private LocationBean location = null;

    /**
     * 取得定位信息
     *
     * @return
     * @author liyinchu
     * @date 2015-7-13下午5:34:00
     */
    public LocationBean getLocation() {
        if (null == location) {
            location = new LocationBean();
            location.province = "广东省"; // 获取省
            location.locationCity = "广州市"; // 获取城市
            location.district = "番禺区"; // 获取区
            location.street = "石壁街道"; // 获取县
        }
        return location;
    }

    /**
     * 开始定位
     *
     * @param context
     */
    public void startLocation(Context context) {
        if (mMyLocation == null) {
            mMyLocation = new MyLocation(mOnLocationResultListener);
        }
        mMyLocation.startRequestLocation(context.getApplicationContext());
    }

    /**
     * 开始定位
     *
     * @param context
     * @param callback
     */
    public void startLocation(Activity context, ILocationListener callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            int permissionCode = context.checkSelfPermission(permission);
            if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(new String[]{permission}, 0);
            }
        }
        this.callback = callback;
        if (mMyLocation == null) {
            mMyLocation = new MyLocation(mOnLocationResultListener);
        }
       mMyLocation.startRequestLocation(context.getApplicationContext());
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (mMyLocation != null) {
            mMyLocation.stopRequestLocation();
            mMyLocation = null;
        }
        this.callback = null;
    }

    /**
     * 定位SDK监听函数
     */
    public OnLocationResultListener mOnLocationResultListener = new OnLocationResultListener() {

        @Override
        public void onLocationResult(int status, AMapLocation amapLocation) {

            if (status == STATUS_OK && amapLocation != null) {
                if (location == null) {
                    location = new LocationBean();
                }
                location.province = amapLocation.getProvince(); // 获取省
                location.locationCity = amapLocation.getCity(); // 获取城市
                location.district = amapLocation.getDistrict(); // 获取区
                location.street = amapLocation.getStreet(); // 获取县
                location.latitude = amapLocation.getLatitude();
                location.longitude = amapLocation.getLongitude();

                if (LocationManager.this.callback != null) {
                    LocationManager.this.callback.onLocationSuccess(location);
                }

            } else {
                if (LocationManager.this.callback != null) {
                    LocationManager.this.callback.onLocationFail();
                }

                /*if (context != null && amapLocation != null && BuildConfig.DEBUG) {
                    ToastUtil.showShort(context,
                            "定位失败:" + amapLocation.getAMapException().getErrorMessage());
                }*/
            }
            stopLocation();
        }
    };

}
