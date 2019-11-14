package me.iwf.photopicker.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tuanmai.tools.Utils.fresco.FrescoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.R;
import me.iwf.photopicker.entity.PhotoDirectory;

/**
 * Created by donglua on 15/6/28.
 */
public final class PopupDirectoryListAdapter extends BaseAdapter {

    private List<PhotoDirectory> directories = new ArrayList<>();

    public PopupDirectoryListAdapter(List<PhotoDirectory> directories) {
        this.directories = directories;
    }


    @Override
    public int getCount() {
        return directories.size();
    }


    @Override
    public PhotoDirectory getItem(int position) {
        return directories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return directories.get(position).hashCode();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
            convertView = mLayoutInflater.inflate(R.layout.__picker_item_directory, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bindData(directories.get(position));

        return convertView;
    }



    private final class ViewHolder {

        public SimpleDraweeView draweeView;
        public TextView tvName;
        public TextView tvCount;

        public ViewHolder(View rootView) {
            draweeView = (SimpleDraweeView) rootView.findViewById(R.id.iv_dir_cover);
            tvName = (TextView) rootView.findViewById(R.id.tv_dir_name);
            tvCount = (TextView) rootView.findViewById(R.id.tv_dir_count);
        }

        public void bindData(PhotoDirectory directory) {
            Uri uri = Uri.fromFile(new File(directory.getCoverPath()));
            FrescoUtils.setDraweeByUri(draweeView, uri);

            tvName.setText(directory.getName());
            tvCount.setText("(" + directory.getPhotos().size() + ")");
        }
    }

}
