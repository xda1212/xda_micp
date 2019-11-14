package com.micp.im.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.micp.im.R;
import com.micp.im.adapter.CommonAdapter;
import com.micp.im.adapter.MyViewHolder;
import com.micp.im.api.remote.ApiUtils;
import com.micp.im.api.remote.PhoneLiveApi;
import com.micp.im.base.ToolBarBaseActivity;
import com.micp.im.bean.UploadBean;
import com.micp.im.ui.customviews.ProgressPopWinFactory;
import com.micp.im.utils.UploadUtil;
import com.tuanmai.tools.Utils.fresco.FrescoUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;

public class PublishCommunity extends ToolBarBaseActivity {

    /**
     * 新增图片
     */
    public final static String CAMERA_ADD = "camera_default";
    private final static String CAMERA_DEFAULT = "camera_default";
    public static final int DATALIST_SIZE = 9;

    private EditText mContentEditText;
    private CommonAdapter<String> mAdapter;
    private GridView mImageList;

    private List<String> mDataList;

    @OnClick({R.id.iv_home_page_back, R.id.publish_community_close})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_home_page_back:
                finish();
                break;
            case R.id.publish_community_close:
                finish();
                break;
        }

    }

    @Override
    public void initView() {
        mImageList = findViewById(R.id.publish_community_image_list);
        mAdapter = new CommonAdapter<String>(this, R.layout.item_publish_community_image) {
            @Override
            public void convert(MyViewHolder viewHolder, String item, final int position) {
                if (item.contains(CAMERA_DEFAULT)) {
                    SimpleDraweeView photo = viewHolder.getView(R.id.iv_photo);
                    FrescoUtils.resId(photo, R.drawable.public_community_list_default_add);
                    viewHolder.setVisibility(R.id.iv_del, View.GONE);
                    viewHolder.setOnClickListener(R.id.iv_del, null);
                } else {
                    SimpleDraweeView photo = viewHolder.getView(R.id.iv_photo);
                    FrescoUtils.file(photo, item);
                    viewHolder.setVisibility(R.id.iv_del, View.VISIBLE);
                    viewHolder.setOnClickListener(R.id.iv_del, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdapter.remove(position);
                            if (!mAdapter.getItem(mAdapter.getCount() - 1).contains(CAMERA_ADD)) {
                                mAdapter.add(CAMERA_ADD);
                            }
                        }
                    });
                }
            }
        };
        mAdapter.add(CAMERA_ADD);
        mImageList.setAdapter(mAdapter);
        mImageList.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mAdapter.getItem(position).contains(CAMERA_ADD)) {
                    goPhotoPicker();
                }
            }
        });

        mContentEditText = findViewById(R.id.publish_community_content);

        final View sendButton = findViewById(R.id.publish_community_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mContentEditText.getText().toString())) {
                    showToast3("请输入发布的主题", 0);
                    return;
                }
                if (null == mDataList || mDataList.size() == 0) {
                    showToast3("请选择发布的图片", 0);
                    return;
                }
                uploadImageToQiNiu();
            }
        });
    }

    //把图片上传到七牛云
    private void uploadImageToQiNiu() {
        ProgressPopWinFactory.getInstance().show(this, "正在发布，请耐心等待...");
        if (null != mDataList && mDataList.size() > 0) {
            List<UploadBean> uploadBeanList = new ArrayList<>();
            for (String uploadFile : mDataList) {
                if (uploadFile.equals(CAMERA_DEFAULT)) {
                    continue;
                }
                File file = new File(uploadFile);
                UploadBean bean = new UploadBean(file, "image_" + file.getName());
                uploadBeanList.add(bean);
            }
            UploadUtil.getInstance().uploadImage(uploadBeanList, new UploadUtil.CallbackImage() {
                @Override
                public void callbackImageSuccess(List<String> imageUrlList) {
                    postCommunity(imageUrlList);
                }

                @Override
                public void callbackImageFail() {
                    ProgressPopWinFactory.getInstance().hide();
                    showToast3("图片上传失败", 0);
                }
            });
        }
    }

    private void postCommunity(List<String> imageList) {
        Gson gson = new Gson();
        String imageListString = gson.toJson(imageList);
        PhoneLiveApi.postCommunity(mContentEditText.getText().toString(), imageListString, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                ProgressPopWinFactory.getInstance().hide();
                showToast3("发布失败", 0);
            }

            @Override
            public void onResponse(String s, int i) {
                ProgressPopWinFactory.getInstance().hide();
                JSONObject res = ApiUtils.checkIsSuccessObject(s);
                if (null != res) {
                    showToast3("发布成功", 0);
                    finish();
                } else {
                    showToast3("发布失败", 0);
                }
            }
        });
    }

    public void goPhotoPicker() {
        List<String> list = new ArrayList<>();
        list.addAll(mAdapter.getData());
        if (mAdapter.getCount() == DATALIST_SIZE) {
            if (list.get(list.size() - 1).contains(CAMERA_ADD)) {
                list.remove(list.size() - 1);
            }
        }
        PhotoPicker.builder()
                .setPhotoCount(DATALIST_SIZE)
                .setGridColumnCount(4)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .setSelected(getIntentArrayList((ArrayList<String>) list))
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {
        ArrayList<String> tDataList = new ArrayList<>();
        for (String s : dataList) {
            if (!s.contains(CAMERA_ADD)) {
                tDataList.add(s);
            }
        }
        return tDataList;
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoPicker.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        mDataList = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        getResultNewList(mDataList);
                    }
                }
                break;
        }
    }

    private void getResultNewList(List<String> photoList) {
        if (photoList != null) {
            if (photoList.size() < DATALIST_SIZE) {
                photoList.add(CAMERA_ADD);
            }
            mAdapter.setNewData(photoList);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_community;
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
