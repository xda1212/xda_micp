package me.iwf.photopicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tuanmai.tools.Utils.fresco.FrescoUtils;

import java.util.ArrayList;

import me.iwf.photopicker.R;

/**
 * 选种列表
 */
public final class GridImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> dataList = new ArrayList<>();
    private DeletePhotoListener deletePhotoListener;


    public ArrayList<String> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    public void setDeletePhotoListener(DeletePhotoListener listener) {
        this.deletePhotoListener = listener;
    }

    public GridImageAdapter(Context c, ArrayList<String> dataList) {
        mContext = c;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //这里不能用ViewHolder，不然会图片错乱
        SimpleDraweeView imgPhoto;
        ImageView imgDel;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.__picker_item_photo_selected, null);
        imgPhoto = (SimpleDraweeView) convertView.findViewById(R.id.iv_photo_selected);
        FrescoUtils.setDraweeImg(imgPhoto, dataList.get(position));

        imgDel = (ImageView) convertView.findViewById(R.id.ic_delete);
        final String path = dataList.get(position);
        imgDel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dataList.remove(position);
                notifyDataSetChanged();
                if (deletePhotoListener != null) {
                    deletePhotoListener.delSelectPhoto(path, dataList.size());
                }
            }
        });
        return convertView;
    }

    public interface DeletePhotoListener {
        void delSelectPhoto(String path, int size);
    }


}