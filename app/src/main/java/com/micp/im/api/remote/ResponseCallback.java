package com.micp.im.api.remote;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.micp.im.AppContext;
import com.micp.im.bean.base.ResponseBase;
import com.micp.im.ui.PhoneLoginActivity;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;

/**
 * @author dengyi
 * @version 1.0
 * @date 2019/8/12
 * @desc
 */
public abstract class ResponseCallback<T> extends StringCallback {
    public final static int SUCCESS_CODE = 200;//成功请求到服务端
    public final static int TOKEN_TIMEOUT = 700;

    private ResponseBase<T> mResponseBean = new ResponseBase<>();

    public abstract void onSuccess(T responseBean);

    public abstract void onFailure(String message);

    @Override
    public void onError(Call call, Exception e, int i) {
        onFailure(e.getMessage());
    }

    @Override
    public void onResponse(String response, int i) {
        if (!TextUtils.isEmpty(response)) {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                Type ty = new ParameterizedTypeImpl(ResponseBase.class, new Type[]{types[0]});
                mResponseBean = new Gson().fromJson(response, ty);
                if (null != mResponseBean && mResponseBean.ret == SUCCESS_CODE) {
                    if (null != mResponseBean.data) {
                        if (mResponseBean.data.code == TOKEN_TIMEOUT) {
                            Intent intent = new Intent(AppContext.getInstance(), PhoneLoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            AppContext.getInstance().startActivity(intent);
                        } else if (mResponseBean.data.code == 0) {
                            onSuccess(mResponseBean.data.info);
                        } else {
                            onFailure(mResponseBean.data.msg);
                        }
                    } else {
                        if (null != mResponseBean && !TextUtils.isEmpty(mResponseBean.msg)) {
                            onFailure(mResponseBean.msg);
                        } else {
                            onFailure("");
                        }
                    }
                } else {
                    onFailure("");
                }
            } else {
                onFailure("");
            }
        } else {
            onFailure("");
        }
    }

    public class ParameterizedTypeImpl implements ParameterizedType {
        private final Class raw;
        private final Type[] args;

        public ParameterizedTypeImpl(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
