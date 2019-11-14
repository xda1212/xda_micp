package com.micp.im.widget.list;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micp.im.R;

public class AutoLoadListView extends TListView {

    private boolean addFooterView;
    private boolean hasMore;
    private boolean autoLoad;
    private OnScrollListener onScrollListener;
    private boolean pauseLoad;
    private final static int MSG_LOADING_TIME_OUT = 0x106;
    private final static int MSG_STARTLOAD_NOW = 0x107;
    private AutoLoadHandler autoHandler;
    private loadMoreListener loadMoreListener;
    private View foot;
    private boolean disablePreLoadOnScroll;
    private ImageView progFooter;
    private TextView tvFooter;
    private RelativeLayout btFooter;

    public AutoLoadListView(Context context) {
        this(context, null, 0);
    }

    public AutoLoadListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.addFooterView = false;
        this.hasMore = false;
        this.autoLoad = true;
        this.onScrollListener = null;
        this.pauseLoad = true;
        this.autoHandler = new AutoLoadHandler(this, Looper.getMainLooper());
        this.disablePreLoadOnScroll = true;

        setDrawingCacheEnabled(false);
        setChildrenDrawingCacheEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);

    }


    class AutoLoadHandler extends Handler {

        final AutoLoadListView autoListView;

        public AutoLoadHandler(AutoLoadListView listView, Looper looper) {
            super(looper);
            this.autoListView = listView;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOADING_TIME_OUT:
                    pauseLoad = true;
                    showGetMoreFail();
                    break;

                case MSG_STARTLOAD_NOW:
                    AutoLoadListView.loadListViewNextPage(AutoLoadListView.this);
                    break;

            }
        }
    }

    class ShowFootRunnable implements Runnable {

        final AutoLoadListView listview;
        final boolean showFooter;

        public ShowFootRunnable(AutoLoadListView autoLoadListView, boolean autoShowFinishFooter) {
            this.listview = autoLoadListView;
            this.showFooter = autoShowFinishFooter;
        }

        @Override
        public void run() {
            boolean showFooter = this.showFooter;
            if (getListViewTotalHeight() < this.listview.getMeasuredHeight()) {
                showFooter = false;
            }

            if (this.listview.getFirstVisiblePosition() > 0) {
                showFooter = true;
            }

            showListViewFinishFoot(listview, showFooter);
            //showListViewFinishFoot(listview, true);
        }
    }


    class FootClickListener implements OnClickListener {

        private final AutoLoadListView listView;
        private loadMoreListener loadMoreListener;

        FootClickListener(AutoLoadListView listView, loadMoreListener listener) {
            this.listView = listView;
            this.loadMoreListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (this.loadMoreListener != null && v.getId() == R.id.list_getmore_foot) {
                loadMoreOnLoading();
                this.loadMoreListener.onLoadMore();
            }

        }
    }

    public interface loadMoreListener {
        void onLoadMore();

        void onLoadMoreFinish();

        void onLoadMoreFail();

        void onLoadMoreSuccessWithMore();
    }


    static void loadListViewNextPage(AutoLoadListView listview) {
        listview.getNextPage();
    }

    static void showListViewFinishFoot(AutoLoadListView listview, boolean showFinishFooter) {
        listview.showFinish(showFinishFooter);
    }

    public void enableAutoLoadMore(Context context, loadMoreListener loadMoreListener) {
        disableAutoLoadMore();
        this.autoLoad = true;
        this.loadMoreListener = loadMoreListener;
        this.foot = LayoutInflater.from(context).inflate(R.layout.tools_footer_autoload, null);
        //this.foot.setVisibility(View.GONE);
        this.btFooter = (RelativeLayout) this.foot.findViewById(R.id.list_getmore_foot);
        this.btFooter.setOnClickListener(new FootClickListener(this, loadMoreListener));
        this.progFooter = (ImageView) this.foot.findViewById(R.id.list_getmore_progress);
        this.tvFooter = (TextView) this.foot.findViewById(R.id.tv_list_getmore_info);

        addFooter();
        removeOnScrollListener(this.onScrollListener);

        this.onScrollListener = new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState != SCROLL_STATE_IDLE) {
                    return;
                }

                if (disablePreLoadOnScroll) {
                    pauseLoad = false;
                    autoHandler.sendEmptyMessageDelayed(MSG_STARTLOAD_NOW, 200);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (!disablePreLoadOnScroll) {
                    pauseLoad = false;
                    autoHandler.sendEmptyMessageDelayed(MSG_STARTLOAD_NOW, 200);
                }

            }
        };
        super.setOnScrollListener(this.onScrollListener);
    }

    public void disableAutoLoadMore() {
        this.autoLoad = false;
        this.loadMoreListener = null;
        removeloadMoreFooterView();
        removeOnScrollListener(this.onScrollListener);
    }

    public void updateHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    private void addFooter() {
        if (!this.addFooterView) {
            addFooterView(this.foot);
            this.addFooterView = true;
        }
    }

    private void removeloadMoreFooterView() {
        if (VERSION.SDK_INT < 19) {
            loadMoreHideFooter();
        } else if (this.addFooterView) {
            removeFooterView(this.foot);
            this.addFooterView = false;
        }
    }

    private void getNextPage() {
        if (this.autoLoad && getLastVisiblePosition() > 0
                && getLastVisiblePosition() >= getCount() - 1
                && this.hasMore
                && getVisibility() == VISIBLE
                && this.loadMoreListener != null
                && !this.pauseLoad) {

            loadMoreOnLoading();
            this.loadMoreListener.onLoadMore();
        }

    }

    public void loadMoreHideFooter() {
        if (this.foot != null) {
            this.foot.setVisibility(View.GONE);
        }
    }

    private void loadMoreOnLoading() {
        this.autoHandler.sendEmptyMessageDelayed(MSG_LOADING_TIME_OUT, 10000);
        if (getCount() == 0) {
            removeloadMoreFooterView();
            return;
        }
        addFooter();
        showWait();
    }

    public void loadMoreOnFail() {
        this.autoHandler.removeMessages(MSG_LOADING_TIME_OUT);

        if (getCount() == 0) {
            removeloadMoreFooterView();
            return;
        }
        this.pauseLoad = true;
        if (this.hasMore) {
            addFooter();
            showGetMoreFail();
            return;
        }
        this.foot.setVisibility(View.GONE);
    }

    public void loadMoreOnFinish() {
        //LogUtil.e("----------------loadMoreOnFinish---------------------");
        loadMoreOnFinish(true);
    }

    public void loadMoreOnFinish(boolean autoShowFinishFooter) {
        this.autoHandler.removeMessages(MSG_LOADING_TIME_OUT);
        if (getCount() == 0) {
            removeloadMoreFooterView();
            return;
        }
        this.pauseLoad = false;
        this.hasMore = false;
        this.autoHandler.post(new ShowFootRunnable(this, autoShowFinishFooter));
    }

    public void loadMoreOnSuccessWithMore() {
        this.autoHandler.removeMessages(MSG_LOADING_TIME_OUT);
        if (getCount() == 0) {
            removeloadMoreFooterView();
            return;
        }
        this.pauseLoad = false;
        this.hasMore = true;
        showGetMore();
    }

    private int getListViewTotalHeight() {
        int count = getChildCount();
        int height = 0;
        if (count > 0) {
            int i = 0;
            while (i < count) {
                try {
                    height += getChildAt(i).getMeasuredHeight();
                    i++;
                } catch (Exception e) {
                }
            }
        }
        return height;
    }

    private void showWait() {
        //加载更多数据时的动画
        this.foot.setVisibility(View.VISIBLE);
        this.progFooter.setImageResource(R.drawable.tools_pull_up_animation);
        ((AnimationDrawable) this.progFooter.getDrawable()).start();
        this.tvFooter.setText("");
        this.btFooter.setClickable(false);
    }

    private void showFinish(boolean showFinishFooter) {
        this.foot.setVisibility(View.VISIBLE);
        this.progFooter.setImageResource(0);
        this.tvFooter.setText("亲，已经看到最后了");
        if (!showFinishFooter) {
            removeloadMoreFooterView();
        }

        this.btFooter.setClickable(false);

    }

    private void showGetMoreFail() {
        this.foot.setVisibility(View.VISIBLE);
        this.progFooter.setImageResource(0);
        this.tvFooter.setText("出错了？点我试试");
        this.btFooter.setClickable(true);
    }

    private void showGetMore() {
        this.foot.setVisibility(View.GONE);
        if (this.progFooter.getDrawable() != null
                && this.progFooter.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) this.progFooter.getDrawable()).stop();
        }
        this.tvFooter.setText("");
        this.btFooter.setClickable(false);
    }

}
