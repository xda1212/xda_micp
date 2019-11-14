package me.iwf.photopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.adapter.GridImageAdapter;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.fragment.PhotoPickerFragment;

import static android.widget.Toast.LENGTH_LONG;
import static me.iwf.photopicker.PhotoPicker.DEFAULT_COLUMN_NUMBER;
import static me.iwf.photopicker.PhotoPicker.DEFAULT_MAX_COUNT;
import static me.iwf.photopicker.PhotoPicker.EXTRA_GRID_COLUMN;
import static me.iwf.photopicker.PhotoPicker.EXTRA_MAX_COUNT;
import static me.iwf.photopicker.PhotoPicker.EXTRA_ORIGINAL_PHOTOS;
import static me.iwf.photopicker.PhotoPicker.EXTRA_PREVIEW_ENABLED;
import static me.iwf.photopicker.PhotoPicker.EXTRA_SHOW_CAMERA;
import static me.iwf.photopicker.PhotoPicker.EXTRA_SHOW_GIF;
import static me.iwf.photopicker.PhotoPicker.KEY_SELECTED_PHOTOS;

public final class PhotoPickerActivity extends AppCompatActivity implements
        View.OnClickListener,GridImageAdapter.DeletePhotoListener{

  private PhotoPickerFragment pickerFragment;
  private ImagePagerFragment imagePagerFragment;

  private int maxCount = DEFAULT_MAX_COUNT;

  private boolean showGif = false;
  private int columnNumber = DEFAULT_COLUMN_NUMBER;
  private ArrayList<String> originalPhotos = null;
  private TextView tvPhotoCount;
  private GridImageAdapter gridAdapter;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if(!Fresco.hasBeenInitialized()){
      ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getApplicationContext())
              .setDownsampleEnabled(true)
              .build();
      Fresco.initialize(getApplicationContext(), config);
    }

    boolean showCamera      = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
    boolean showGif         = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
    boolean previewEnabled  = getIntent().getBooleanExtra(EXTRA_PREVIEW_ENABLED, true);

    setShowGif(showGif);

    setContentView(R.layout.__picker_activity_photo_picker);

    GridView gvImageList = (GridView) findViewById(R.id.gv_image_list);
    tvPhotoCount = (TextView) findViewById(R.id.tv_photo_count);
    tvPhotoCount.setOnClickListener(this);

    maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
    columnNumber = getIntent().getIntExtra(EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER);
    originalPhotos = getIntent().getStringArrayListExtra(EXTRA_ORIGINAL_PHOTOS);

    gridAdapter = new GridImageAdapter(this,originalPhotos);
    gridAdapter.setDeletePhotoListener(this);
    gvImageList.setAdapter(gridAdapter);
    //初始化底部布局
    if (originalPhotos != null){
      tvPhotoCount.setText(getString(R.string.__picker_done_with_count_new, originalPhotos.size(), maxCount));
    }

    pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentByTag("tag");
    if (pickerFragment == null) {
      pickerFragment = PhotoPickerFragment
          .newInstance(showCamera, showGif, previewEnabled, columnNumber, maxCount, originalPhotos);
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.container, pickerFragment, "tag")
          .commit();
      getSupportFragmentManager().executePendingTransactions();
    }

    pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
      @Override public boolean OnItemCheck(final int position, final Photo photo, final boolean isCheck, final int selectedItemCount) {

        int total = selectedItemCount + (isCheck ? -1 : 1);

        if (maxCount <= 1) {
          List<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
          if (!photos.contains(photo.getPath())) {
            photos.clear();
            pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
          }
          return true;
        }

        if (total > maxCount) {
          Toast.makeText(getActivity(), getString(R.string.__picker_over_max_count_tips, maxCount),
              LENGTH_LONG).show();
          return false;
        }

        tvPhotoCount.setText(getString(R.string.__picker_done_with_count_new, total, maxCount));

        if (!isCheck) {
          gridAdapter.getDataList().add(photo.getPath());
        }else {
          gridAdapter.getDataList().remove(photo.getPath());
        }
        gridAdapter.notifyDataSetChanged();
        return true;
      }
    });

  }

  /**
   * Overriding this method allows us to run our exit animation first, then exiting
   * the activity when it complete.
   */
  @Override public void onBackPressed() {
    if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
      imagePagerFragment.runExitAnimation(new Runnable() {
        public void run() {
          if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
          }
        }
      });
    } else {
      super.onBackPressed();
    }
  }


  public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
    this.imagePagerFragment = imagePagerFragment;
    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.container, this.imagePagerFragment)
        .addToBackStack(null)
        .commit();
  }


  public PhotoPickerActivity getActivity() {
    return this;
  }

  public GridImageAdapter getAdapter(){
    return gridAdapter;
  }

  public void setTextCount(int count){
    tvPhotoCount.setText(getString(R.string.__picker_done_with_count_new, count, maxCount));
  }

  public boolean isShowGif() {
    return showGif;
  }

  public void setShowGif(boolean showGif) {
    this.showGif = showGif;
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.tv_photo_count) {
       Intent intent = new Intent();
       ArrayList<String> selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
       intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
       setResult(RESULT_OK, intent);
       finish();
    }
  }

  @Override
  public void delSelectPhoto(String path, int size) {
    tvPhotoCount.setText(getString(R.string.__picker_done_with_count_new, size, maxCount));
    List<String> selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
    if (!TextUtils.isEmpty(path) && selectedPhotos.contains(path)){
      selectedPhotos.remove(path);
    }
    pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
  }


}
