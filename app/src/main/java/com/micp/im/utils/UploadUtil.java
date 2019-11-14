package com.micp.im.utils;

import android.util.Log;

import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.bean.UploadBean;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by cxf on 2017/7/12.
 */

public class UploadUtil {
    private static UploadUtil sInstance;
    private UploadManager mUploadManager;
    private final String TAG = "UploadUtil";
    private String mToken;

    private UploadUtil() {
        mUploadManager = new UploadManager(new Configuration.Builder().zone(Zone.zone2).build());
    }

    public static UploadUtil getInstance() {
        if (sInstance == null) {
            synchronized (UploadUtil.class) {
                if (sInstance == null) {
                    sInstance = new UploadUtil();
                }
            }
        }
        return sInstance;
    }

    public void uploadVideo(final UploadBean bean, final Callback onCompleteCallback) {
        PhoneLiveApi.getQiniuToken(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (null != onCompleteCallback) {
                    onCompleteCallback.callbackFail(bean);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    mToken = res.getJSONObject(0).getString("token");
                    Log.e(TAG, "onResponse: -------上传的token------>" + mToken);
                    mUploadManager.put(bean.getVideo(), bean.getVideoName(), mToken, new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            Log.e(TAG, "complete: -key--> " + key);
                            Log.e(TAG, "complete: --info-> " + key);
                            Log.e(TAG, "complete: --response---> " + response);
                            mUploadManager.put(bean.getCoverPic(), bean.getCoverPicName(), mToken, new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                    Log.e(TAG, "complete: -key--> " + key);
                                    Log.e(TAG, "complete: --info-> " + key);
                                    Log.e(TAG, "complete: --response---> " + response);
                                    if (info.isOK()) {
                                        if (onCompleteCallback != null) {
                                            onCompleteCallback.callbackSuccess(bean);
                                        }
                                    } else {
                                        if (null != onCompleteCallback) {
                                            onCompleteCallback.callbackFail(bean);
                                        }
                                    }
                                }
                            }, null);
                        }
                    }, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void uploadImage(final List<UploadBean> uploadBeanList, final CallbackImage onCompleteCallback) {
        PhoneLiveApi.getQiniuToken(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (null != onCompleteCallback) {
                    onCompleteCallback.callbackImageFail();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    mToken = res.getJSONObject(0).getString("token");
                    uploadImage(uploadBeanList, 0, onCompleteCallback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //递归上传
    private void uploadImage(final List<UploadBean> uploadBeanList, final int index, final CallbackImage onCompleteCallback) {
        mUploadManager.put(uploadBeanList.get(index).getCoverPic(), uploadBeanList.get(index).getCoverPicName(), mToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                Log.e(TAG, "complete: -key--> " + key);
                Log.e(TAG, "complete: --info-> " + key);
                Log.e(TAG, "complete: --response---> " + response);
                if (info.isOK()) {
                    int nextIndex = index + 1;
                    if (nextIndex < uploadBeanList.size()) {
                        uploadImage(uploadBeanList, nextIndex, onCompleteCallback);
                    } else {
                        if (onCompleteCallback != null) {
                            List<String> result = new ArrayList<>();
                            for (UploadBean uploadBean : uploadBeanList) {
                                result.add(uploadBean.getCoverPicName());
                            }
                            onCompleteCallback.callbackImageSuccess(result);
                        }
                    }
                } else {
                    if (null != onCompleteCallback) {
                        onCompleteCallback.callbackImageFail();
                    }
                }
            }
        }, null);
    }

    public interface Callback {
        void callbackSuccess(UploadBean bean);

        void callbackFail(UploadBean bean);
    }

    public interface CallbackImage {
        void callbackImageSuccess(List<String> imageUrlList);

        void callbackImageFail();
    }
}
