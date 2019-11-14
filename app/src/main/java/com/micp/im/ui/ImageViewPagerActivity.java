package com.micp.im.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.micp.im.R;
import com.micp.im.widget.viewpager.HackyViewPager;
import com.tuanmai.tools.Utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.PhotoDraweeView;


public final class ImageViewPagerActivity extends Activity implements OnPageChangeListener {

    public static final String EXTRA_IMAGE_LIST = "extra_image_list";
    public static final String EXTRA_POSITION = "extra_position";
    /**
     * ViewPager
     */
    private HackyViewPager viewPager;

    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    /**
     * 装ImageView数组
     */
    private PhotoDraweeView[] mImageViews;

    /**
     * 图片资源id
     */
    private List<String> imgIdArray;
    private int index;

    private LinearLayout group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewpager);
        initView();
        initData();
        initImage();
    }

    private void initImage() {
        // 将点点加入到ViewGroup中
        tips = new ImageView[imgIdArray.size()];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            int size = ScreenUtil.dp2px(7);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.leftMargin = ScreenUtil.dp2px(5);
            imageView.setLayoutParams(layoutParams);
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.icon_banner_item_selected);
            } else {
                tips[i].setBackgroundResource(R.drawable.icon_banner_item_normal);
            }

            group.addView(imageView);
        }

        // 将图片装载到数组中
        mImageViews = new PhotoDraweeView[imgIdArray.size()];
        // mViews=new View[imgIdArray.size()];
        for (int i = 0; i < mImageViews.length; i++) {
            PhotoDraweeView imageView = new PhotoDraweeView(this);
            //高版本才支持转场动画
            if (Build.VERSION.SDK_INT >= 21) {
                imageView.setTransitionName("goodsTrans");
            }
            mImageViews[i] = imageView;
            imageView.setPhotoUri(Uri.parse(imgIdArray.get(i)));
            mImageViews[i].setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    onBackPressed();
                }
            });
        }


        // 设置Adapter
        viewPager.setAdapter(new MyAdapter());
        // 设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(this);
        // 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.setCurrentItem(index);
    }

    private void initView() {
        group = (LinearLayout) findViewById(R.id.lin_layout);
        viewPager = (HackyViewPager) findViewById(R.id.viewPager);
    }

    private void initData() {
        // 载入图片资源ID
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        imgIdArray = (ArrayList<String>) bundle.getSerializable(EXTRA_IMAGE_LIST);
        index = intent.getIntExtra(EXTRA_POSITION, 0);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        setImageBackground(arg0);
    }

    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.icon_banner_item_selected);
            } else {
                tips[i].setBackgroundResource(R.drawable.icon_banner_item_normal);
            }
        }

    }

    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    /**
     * @author xiaanming
     */
    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViews == null ? 0 : mImageViews.length;
            // return mViews == null ? 0 : mViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mImageViews[position]);
            // ((ViewPager) container).removeView(mViews[position]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mImageViews[position], 0);
            return mImageViews[position];
            // ((ViewPager) container).addView(mViews[position], 0);
            // return mViews[position];
        }
    }
}
