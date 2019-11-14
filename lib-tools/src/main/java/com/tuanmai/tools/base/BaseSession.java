package com.tuanmai.tools.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.tuanmai.tools.manager.ObserverManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observer;
import java.util.UUID;

/**
 * Created by LiuQiCong
 *
 * @date 2017-07-12 15:25
 * version 1.0
 * dsc Session基类
 */
public class BaseSession {


    public static Context mContext;

    public BaseSession(){

    }

    public static void init(Context context) {
        if (null == mContext) {
            mContext = context.getApplicationContext();
        }
    }


    //======================================本地保存对象===========================================
    private SharedPreferences sp;
    public SharedPreferences getSP() {
        if (sp == null) {
            sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        }
        return sp;
    }


    //=======================================UUID================================================
    private String uuid;
    public String getUUID() {
        if (TextUtils.isEmpty(uuid)) {
            uuid = getSP().getString("uuid", "");
            if (TextUtils.isEmpty(uuid)) uuid = readUUID(Environment.getExternalStorageDirectory());
            if (TextUtils.isEmpty(uuid)) uuid = readUUID(getDefaultPath());
            //先使用淘宝提供的设备标识生成库生成
//            if (TextUtils.isEmpty(uuid)) uuid = UTDevice.getUtdid(mContext);
            if (TextUtils.isEmpty(uuid)) uuid = UUID.randomUUID().toString();
            saveUUID(uuid);
        }
        return uuid;
    }

    private String readUUID(File fileDir) {
        File file = new File(fileDir, ".global_uuid");
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                return br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressLint("SdCardPath")
    private void saveUUID(String uuid) {
        if (!getSP().contains("uuid")) {
            SharedPreferences.Editor editor = getSP().edit();
            editor.putString("uuid", uuid);
            editor.commit();
        }
        writUUID(Environment.getExternalStorageDirectory());
        writUUID(getDefaultPath());
    }

    private void writUUID(File fileDir) {
        try {
            File file = new File(fileDir, ".global_uuid");
            if (file.exists()) return;
            if (file.createNewFile()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(uuid);
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //=======================================重新挂载SDCard===========================================
    public void remountSDcard() {
        if (null != mContext) {
            Intent intent = new Intent();
            // 重新挂载的动作
            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
            // 要重新挂载的路径
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            mContext.sendBroadcast(intent);
        }
    }


    //=========================================common method========================================
    private File defaultPath;
    public File getDefaultPath() {
        if (defaultPath == null || !defaultPath.exists()) {
            boolean sdEnable=Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
            String rootPath=sdEnable?Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
            defaultPath = new File(rootPath + "/AoMyGod");
            if (!defaultPath.exists()) {
                defaultPath.mkdirs();
            }
        }
        return defaultPath;
    }

    @SuppressLint("SdCardPath")
    public void writeLog(String log) {
        try {
            File file = new File(getDefaultPath(), System.currentTimeMillis() + "_log.txt");
            if (file.exists()) return;
            if (file.createNewFile()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(log);
                bw.close();
            }
            //LogUtil.e("-------------log写完---------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //======================================观察者模式相关通知=======================================
    public void notifyData(String key, Object data) {
        ObserverManager.getInstance().notifyData(key, data);
    }

    public void notifyData(String key) {
        notifyData(key, "");
    }

    public void addObserver(Observer observer){
        ObserverManager.getInstance().addObserver(observer);
    }

    public void deleteObserver(Observer observer){
        ObserverManager.getInstance().deleteObserver(observer);
    }

    //====================================================================================

}
