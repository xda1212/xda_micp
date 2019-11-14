package com.tuanmai.tools.manager;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuQiCong
 * date 2017-08-04 15:52
 * version 1.0
 * dsc 活动管理者
 */
public final class AmgActivityManager {

    private static AmgActivityManager mInstance;
    private Activity activity;

    private AmgActivityManager() {}

    /**
     * 获取单一实例
     */
    public static AmgActivityManager getInstance() {
        if (mInstance == null) {
            synchronized (AmgActivityManager.class) {
                if (mInstance == null) {
                    mInstance = new AmgActivityManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * 保存当前Activity
     */
    public void setCurActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 获取当前Activity
     */
    public Activity getCurActivity(){
        return activity;
    }



    //================================管理所有的activity================================
    private List<Activity> activities=new ArrayList<>();

    /**
     *  添加activity
     * @param activity
     */
    public void addActivity(Activity activity){
       /* int size=activities.size();
        Activity itemActivity;
        for(int i=size-1;i>=0;--i){
            itemActivity=activities.get(i);
            if(itemActivity.isFinishing()){

                activities.remove(i);
            }
        }
        //LogUtil.e("===========activities==========>"+activities.size());
        activities.add(activity);*/
    }

    public List<Activity> getActivities(){
        return activities;
    }



}
