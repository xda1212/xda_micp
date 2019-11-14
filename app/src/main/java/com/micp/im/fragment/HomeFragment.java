package com.micp.im.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.micp.im.AppContext;
import com.micp.im.R;
import com.micp.im.base.AbsFragment;
import com.micp.im.ui.MainActivity;
import com.micp.im.ui.OrderWebViewActivity;
import com.micp.im.ui.StartLiveActivity;
import com.micp.im.ui.customviews.ViewPagerIndicator;
import com.micp.im.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends AbsFragment implements MainActivity.OnResumeCallback {

    private View mRootView;
    private ViewPagerIndicator mIndicator;
    private ViewPager mViewPager;

    private List<Fragment> mFragmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        mIndicator = (ViewPagerIndicator) mRootView.findViewById(R.id.indicator);
        mIndicator.setTitles(new String[]{"社区", "附近", "关注"});
        mIndicator.setVisibleChildCount(3);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        if (savedInstanceState == null) {
            mFragmentList = new ArrayList<>();
            CommunityFragment f1 = new CommunityFragment();
            NearUserFragment f2 = new NearUserFragment();
            AttentionUserFragment f3 = new AttentionUserFragment();
//            NearFragment f2 = new NearFragment();
//            GuanzhuFragment f3 = new GuanzhuFragment();
//            PersontoPersonFragment f4 = new PersontoPersonFragment();
            mFragmentList.add(f1);
            mFragmentList.add(f2);
            mFragmentList.add(f3);
//            fragmentList.add(f4);
            mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return mFragmentList.get(position);
                }

                @Override
                public int getCount() {
                    return mFragmentList.size();
                }
            });
            mViewPager.setOffscreenPageLimit(2);
            mIndicator.setViewPager(mViewPager);
            mIndicator.setListener(new ViewPagerIndicator.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    publishPosition(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        mRootView.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showScreen(getActivity());
            }
        });
    }

    private void publishPosition(int position) {
        if (position == 0) {
            CommunityFragment communityFragment = (CommunityFragment) mFragmentList.get(position);
            communityFragment.updateListData();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            publishPosition(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onResumeRefresh() {
        publishPosition(mViewPager.getCurrentItem());
    }
}
