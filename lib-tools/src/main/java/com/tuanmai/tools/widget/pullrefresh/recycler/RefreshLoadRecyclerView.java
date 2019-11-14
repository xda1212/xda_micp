package com.tuanmai.tools.widget.pullrefresh.recycler;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tuanmai.tools.R;
import com.tuanmai.tools.widget.pullrefresh.PullToRefreshBase;
import com.tuanmai.tools.widget.pullrefresh.internal.LoadingLayout;

/**
 * Created by LiuQiCong
 *
 * @date 2017-05-11 13:57
 * version 1.0
 * dsc 基于RecyclerView的下拉刷新上拉加载控件
 */
public class RefreshLoadRecyclerView extends LinearLayout {

    private int mLastMotionY;

    private RecyclerView mRecyclerView;
    private State mHeaderState;
    private State mPullState;
    private boolean headEnable = true;
    private boolean footEnable = true;
    private OnRefreshLoadListener mFreshListener;

    private LoadingLayout mHeaderLayout;
    private FooterView mFooterLayout;
    private int mHeaderViewHeight;

    private PullToRefreshBase.AnimationStyle mLoadingAnimationStyle = PullToRefreshBase.AnimationStyle.getDefault();


    public RefreshLoadRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public RefreshLoadRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RefreshLoadRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RefreshLoadRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        // Styleables from XML
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.PullToRefresh);

        if (a.hasValue(R.styleable.PullToRefresh_ptrAnimationStyle)) {
            mLoadingAnimationStyle = PullToRefreshBase.AnimationStyle.mapIntToValue(a.getInteger(
                    R.styleable.PullToRefresh_ptrAnimationStyle, 0));
        }

        // We need to create now layouts now
        mHeaderLayout = createLoadingLayout(context, PullToRefreshBase.Mode.PULL_FROM_START, a);
        // Let the derivative classes have a go at handling attributes, then
        // recycle them...
        //handleStyledAttributes(a);
        a.recycle();

        if (getChildCount() > 0) {
            removeAllViews();
        }

        addHeaderView();
        initRecyclerView(context);

    }

    private void initRecyclerView(Context context) {
        mRecyclerView = new RecyclerView(context);
        addView(mRecyclerView, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        ));
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected LoadingLayout createLoadingLayout(Context context, PullToRefreshBase.Mode mode, TypedArray attrs) {
        LoadingLayout layout = mLoadingAnimationStyle.createLoadingLayout(context, mode,
                PullToRefreshBase.Orientation.VERTICAL, attrs);
        layout.setVisibility(View.INVISIBLE);
        return layout;
    }

    private void addHeaderView() {
        // header view
        addView(mHeaderLayout, 0);
        mHeaderLayout.setVisibility(View.VISIBLE);
        mHeaderLayout.pullToRefresh();

        measureView(mHeaderLayout);
        mHeaderViewHeight = mHeaderLayout.getMeasuredHeight();
        LayoutParams params = (LayoutParams) mHeaderLayout.getLayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = mHeaderViewHeight;
        params.topMargin = -mHeaderViewHeight;
        mHeaderLayout.setLayoutParams(params);
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec = lpHeight > 0 ? MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY) : MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 拦截事件，判断是否处理
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:// 首先拦截down事件,记录y坐标
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:// deltaY > 0 是向下运动,< 0是向上运动
                int deltaY = y - mLastMotionY;
                int baseGap = 5;
                if (Math.abs(deltaY) < baseGap) return false;
                if (deltaY > baseGap && !headEnable) return false;
                if (deltaY < -baseGap && !footEnable) return false;
                if (isRefreshViewScroll(deltaY)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastMotionY;
                if (headEnable && mPullState == State.PULL_DOWN_STATE) {//执行下拉
                    headerPrepareToRefresh(deltaY);

                }
                mLastMotionY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                LayoutParams params = (LayoutParams) mHeaderLayout.getLayoutParams();
                int topMargin = params.topMargin;
                if (headEnable && mPullState == State.PULL_DOWN_STATE) {
                    if (topMargin >= 0) {
                        headerRefreshing(true);
                    } else {
                        setHeaderTopMargin(-mHeaderViewHeight);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否应该到了父View,即PullToRefreshView滑动
     * deltaY > 0 是向下运动,< 0是向上运动
     */
    private boolean isRefreshViewScroll(int deltaY) {
        if (deltaY < 0 || mHeaderState == State.REFRESHING) return false;
        //只处理下拉刷新
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
            if (mRecyclerView.getChildCount() == 0) {
                mPullState = State.PULL_DOWN_STATE;
                return true;
            }

            View child = manager.getChildAt(0);
            if (null == child) return false;
            if (manager.findFirstVisibleItemPosition() == 0 && child.getTop() == 0) {
                mPullState = State.PULL_DOWN_STATE;
                return true;
            }
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) layoutManager;

            if (mRecyclerView.getChildCount() == 0) {
                mPullState = State.PULL_DOWN_STATE;
                return true;
            }

            View child = manager.getChildAt(0);
            if (null == child) return false;
            if (manager.findFirstVisibleItemPosition() == 0 && child.getTop() == 0) {
                mPullState = State.PULL_DOWN_STATE;
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            if (manager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                if (mRecyclerView.getChildCount() == 0) {
                    mPullState = State.PULL_DOWN_STATE;
                    return true;
                }

                View child = manager.getChildAt(0);
                if (null == child) return false;
                if (child.getTop() == 0) {
                    mPullState = State.PULL_DOWN_STATE;
                    return true;
                }


            }


        }
        return false;
    }

    /**
     * header 准备刷新,手指移动过程,还没有释放
     *
     * @param deltaY 手指滑动的距离
     */
    private void headerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
        if (newTopMargin > 0 && mHeaderState != State.RELEASE_TO_REFRESH) {
            mHeaderState = State.RELEASE_TO_REFRESH;
            //mHeaderLayout.pullToRefresh();

        } else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {// 拖动时没有释放
            mHeaderState = State.PULL_TO_REFRESH;
            //mHeaderLayout.pullToRefresh();
        }
    }

    /**
     * 修改Header view top margin的值
     */
    private int changingHeaderViewTopMargin(int deltaY) {
        LayoutParams params = (LayoutParams) mHeaderLayout.getLayoutParams();
        float newTopMargin = params.topMargin + deltaY * 0.4f;
        //这里对上拉做一下限制,因为当前上拉后然后不释放手指直接下拉,会把下拉刷新给触发了
        //表示如果是在上拉后一段距离,然后直接下拉
        if (deltaY > 0
                && mPullState == State.PULL_UP_STATE
                && Math.abs(params.topMargin) <= mHeaderViewHeight) {
            return params.topMargin;
        }
        //同样地,对下拉做一下限制,避免出现跟上拉操作时一样的bug
        if (deltaY < 0
                && mPullState == State.PULL_DOWN_STATE
                && Math.abs(params.topMargin) >= mHeaderViewHeight) {
            return params.topMargin;
        }
        params.topMargin = (int) newTopMargin;
        mHeaderLayout.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    public void simulationHeadRefreshing() {
        headerRefreshing(false);
    }

    /**
     * header refreshing
     */
    private void headerRefreshing(boolean listenerEnable) {
        mHeaderState = State.REFRESHING;
        setHeaderTopMargin(0);

        if (listenerEnable && null != mFreshListener) {
            mFreshListener.onRefresh();
        }
    }


    /**
     * 设置header view 的topMargin的值
     *
     * @param topMargin 为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
     */
    private void setHeaderTopMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderLayout.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderLayout.setLayoutParams(params);
    }

    /**
     * header view 完成更新后恢复初始状态
     */
    public void onHeaderRefreshComplete() {
        if (mHeaderState != State.PULL_TO_REFRESH) {
            mHeaderState = State.PULL_TO_REFRESH;
            LayoutParams params = (LayoutParams) mHeaderLayout.getLayoutParams();
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mHeaderLayout, "xyz", params.topMargin, -mHeaderViewHeight).setDuration(500);
            objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setHeaderTopMargin((int) animation.getAnimatedValue());
                }
            });
            objectAnimator.start();
        }
    }

    /**
     * footer view 完成更新后恢复初始状态
     */
    public void onFooterRefreshComplete() {
        if (null != mFooterLayout) {
            mFooterLayout.showFinish();
        }
    }

    public void setOnRefreshLoadListener(OnRefreshLoadListener listener, boolean headE, boolean footE) {
        mFreshListener = listener;
        headEnable = headE;
        footEnable = footE;
        if (footEnable && null == mFooterLayout) {
            // footer view 在此添加保证添加到linearlayout中的最后
            mFooterLayout = new FooterView(getContext());
        }
    }

    private enum State {
        // pull state
        PULL_UP_STATE(0),
        PULL_DOWN_STATE(1),
        // refresh states
        PULL_TO_REFRESH(2),
        RELEASE_TO_REFRESH(3),
        REFRESHING(4);

        final int nativeInt;

        State(int ni) {
            this.nativeInt = ni;
        }
    }
}
