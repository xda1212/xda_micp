package com.tuanmai.tools.entity;

/**
 * @author LiuQiCong
 * @date 2016-11-09 15:53
 * version 1.0
 * dsc 观察者传递的数据的容器类
 */
public final class Notify {

    private String mKey;
    private Object mData;

    public Notify(String key, Object data){
        mKey=key;
        mData=data;
    }

    public boolean equalsKey(String key){
        return mKey.equals(key);
    }

    public Object getData(){return mData;}

}
