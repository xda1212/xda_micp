package com.tuanmai.tools.Utils.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MapUtils {

    private static final String TAG = "MapUtils";

    /**
     * 打开高德web
     *
     * @param activity
     * @param longitude 经度
     * @param latitude  纬度
     */
    public static void openWebGaoDe(Activity activity, double longitude, double latitude) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse("http://uri.amap.com/marker?position=" + longitude + "," + latitude + ""));
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开高德地图
     *
     * @param activity
     * @param locationContent 地址描述
     * @param longitude       经度
     * @param latitude        纬度
     */
    public static void openGaoDeMap(Activity activity, String locationContent, double longitude, double latitude) {
        String act = "android.intent.action.VIEW";
        String dat = "androidamap://viewMap?sourceApplication=&poiname=" + locationContent + "&lat=" + latitude + "&lon=" + longitude + "&dev=0";
        String pkg = "com.autonavi.minimap";
        try {
            Intent intent = new Intent(act, android.net.Uri.parse(dat));
            intent.setPackage(pkg);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开百度地图
     *
     * @param activity
     * @param locationTitle   地址标题
     * @param locationContent 地址描述
     * @param longitude       经度
     * @param latitude        纬度
     */
    public static void openBaiDuMap(Activity activity, String locationTitle, String locationContent, double longitude, double latitude) {
        boolean installBaiDu = MapUtils.isInstallApk(activity, "com.baidu.BaiduMap");
        try {
            double[] doubles = GPSUtil.gcj02_To_Bd09(latitude, longitude);
            Intent intent = Intent.getIntent("intent://map/marker?location=" + doubles[0] + "," + doubles[1] +
                    "&title=" + locationTitle + "&content=" + locationContent + "&src=|#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            if (installBaiDu) {
                activity.startActivity(intent); //启动调用
            } else {
                Toast.makeText(activity, "没有安装百度地图", Toast.LENGTH_SHORT).show();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param addr 查询的地址
     * @return
     * @throws IOException
     */
    public static Object[] getCoordinate(String addr) throws IOException {
        String lng = null;//经度  
        String lat = null;//纬度  
        String address = "";
        try {
            address = java.net.URLEncoder.encode(addr, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String key = "iQWBRMOXBiUa1WWmGONiGaOZ";
        String url = String.format("http://api.map.baidu.com/geocoder?address=%s&output=json&key=%s", address, key);
        //        String url = "http://api.map.baidu.com/geocoder/v2/?address=北京市海淀区上地十街10号&output=json&ak="+key;
        url = "http://restapi.amap.com/v3/geocode/geo?key=12a7a70be474a46ef5854686fa1ea42d&address=大石街道&city=广州";
        Log.e(TAG, "getCoordinate: " + url);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader insr = null;
        BufferedReader br = null;
        try {
            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理  
            if (httpsConn != null) {
                insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                br = new BufferedReader(insr);
                String data = null;
                int count = 1;
                while ((data = br.readLine()) != null) {
                    if (count == 5) {
                        lng = (String) data.subSequence(data.indexOf(":") + 1, data.indexOf(","));//经度  
                        count++;
                    } else if (count == 6) {
                        lat = data.substring(data.indexOf(":") + 1);//纬度  
                        count++;
                    } else {
                        count++;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (insr != null) {
                insr.close();
            }
            if (br != null) {
                br.close();
            }
        }
        Log.e("MapUtils", "精度===" + lng + "=纬度===" + lat);
        return new Object[]{lng, lat};
    }

    /**
     * 判断手机中是否安装指定包名的软件
     */
    public static boolean isInstallApk(Context context, String name) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals(name)) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }


    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }


    class LatLng {

        public double latitude;
        public double longitude;
    }

    /**
     * 高德地图获取经纬度点直接距离
     */
    public static float calculateLineDistance(LatLng var0, LatLng var1) {
        if (var0 != null && var1 != null) {
            try {
                double var2 = 0.01745329251994329D;
                double var4 = var0.longitude;
                double var6 = var0.latitude;
                double var8 = var1.longitude;
                double var10 = var1.latitude;
                var4 *= 0.01745329251994329D;
                var6 *= 0.01745329251994329D;
                var8 *= 0.01745329251994329D;
                var10 *= 0.01745329251994329D;
                double var12 = Math.sin(var4);
                double var14 = Math.sin(var6);
                double var16 = Math.cos(var4);
                double var18 = Math.cos(var6);
                double var20 = Math.sin(var8);
                double var22 = Math.sin(var10);
                double var24 = Math.cos(var8);
                double var26 = Math.cos(var10);
                double[] var28 = new double[3];
                double[] var29 = new double[3];
                var28[0] = var18 * var16;
                var28[1] = var18 * var12;
                var28[2] = var14;
                var29[0] = var26 * var24;
                var29[1] = var26 * var20;
                var29[2] = var22;
                double var30 = Math.sqrt((var28[0] - var29[0]) * (var28[0] - var29[0]) + (var28[1] - var29[1]) * (var28[1] - var29[1]) + (var28[2] - var29[2]) * (var28[2] - var29[2]));
                return (float) (Math.asin(var30 / 2.0D) * 1.27420015798544E7D);
            } catch (Throwable var32) {
                var32.printStackTrace();
                return 0.0F;
            }
        }
        return 0;
    }
}