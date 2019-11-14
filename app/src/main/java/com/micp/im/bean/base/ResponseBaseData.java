package com.micp.im.bean.base;

/**
 * @author dengyi
 * @version 1.0
 * @date 2019/8/12
 * @desc
 */
public class ResponseBaseData<T> {
    public int code;
    public String msg;
    public T info;
}
