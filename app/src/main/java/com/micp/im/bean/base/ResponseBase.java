package com.micp.im.bean.base;

/**
 * @author dengyi
 * @version 1.0
 * @date 2019/8/12
 * @desc
 */
public class ResponseBase<T> {
    public int ret;
    public String msg;
    public ResponseBaseData<T> data;
}
