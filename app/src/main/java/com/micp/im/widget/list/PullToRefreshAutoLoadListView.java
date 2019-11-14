package com.micp.im.widget.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.micp.im.R;
import com.micp.im.widget.list.pullrefresh.LoadingLayoutProxy;
import com.micp.im.widget.list.pullrefresh.PullToRefreshAdapterViewBase;
import com.micp.im.widget.list.pullrefresh.internal.EmptyViewMethodAccessor;
import com.micp.im.widget.list.pullrefresh.internal.LoadingLayout;

/**
 * Created by Administrator on 2016/3/21.
 */
public class PullToRefreshAutoLoadListView extends PullToRefreshAdapterViewBase<AutoLoadListView> {

    private LoadingLayout mHeaderLoadingView;
    private LoadingLayout mFooterLoadingView;
    private FrameLayout mLvFooterLoadingFrame;
    private boolean mListViewExtrasEnabled;

    public PullToRefreshAutoLoadListView(Context context) {
        super(context);
    }

    public PullToRefreshAutoLoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshAutoLoadListView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshAutoLoadListView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    /**
     * @return Either {@link Orientation#VERTICAL} or
     * {@link Orientation#HORIZONTAL} depending on the scroll direction.
     */
    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }


    @Override
    protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
        LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart,includeEnd);

        if (mListViewExtrasEnabled) {
            final Mode mode = getMode();
            if (includeStart && mode.showHeaderLoadingLayout()) {
                proxy.addLayout(mHeaderLoadingView);
            }
            if (includeEnd && mode.showFooterLoadingLayout()) {
                proxy.addLayout(mFooterLoadingView);
            }
        }
        return proxy;
    }

    protected AutoLoadListView createListView(Context context, AttributeSet attrs) {
        final AutoLoadListView lv;
        lv = new InternalListView(context, attrs);
        return lv;
    }

    /**
     * This is implemented by derived classes to return the created View. If you
     * need to use a custom View (such as a custom ListView), override this
     * method and return an instance of your custom class.
     * <p/>
     * Be sure to set the ID of the view in this method, especially if you're
     * using a ListActivity or ListFragment.
     *
     * @param context Context to create view with
     * @param attrs   AttributeSet from wrapped class. Means that anything you
     *                include in the XML layout declaration will be routed to the
     *                created View
     * @return New instance of the Refreshable View
     */
    @Override
    protected AutoLoadListView createRefreshableView(Context context, AttributeSet attrs) {
        AutoLoadListView lv = createListView(context, attrs);
        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        return lv;
    }

    @Override
    protected void onRefreshing(boolean doScroll) {
        /**
         * If we're not showing the Refreshing view, or the list is empty, the
         * the header/footer views won't show so we use the normal method.
         */
        ListAdapter adapter = mRefreshableView.getAdapter();
        if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing()
                || null == adapter || adapter.isEmpty()) {
            super.onRefreshing(doScroll);
            return;
        }

        super.onRefreshing(false);

        final int selection, scrollToY;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                LoadingLayout origLoadingView = getFooterLayout();
                LoadingLayout listViewLoadingView = mFooterLoadingView;
                LoadingLayout oppositeListViewLoadingView = mHeaderLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToY = getScrollY() - getFooterSize();

                // Hide our original Loading View
                origLoadingView.reset();
                origLoadingView.hideAllViews();

                // Make sure the opposite end is hidden too
                oppositeListViewLoadingView.setVisibility(View.GONE);

                // Show the ListView Loading View and set it to refresh.
                listViewLoadingView.setVisibility(View.VISIBLE);
                listViewLoadingView.refreshing();
                break;

            case PULL_FROM_START:
            default:
                LoadingLayout origLoadingView2 = getHeaderLayout();
                LoadingLayout listViewLoadingView2 = mHeaderLoadingView;
                LoadingLayout oppositeListViewLoadingView2 = mFooterLoadingView;
                selection = 0;
                scrollToY = getScrollY() + getHeaderSize();

                // Hide our original Loading View
                origLoadingView2.reset();
                origLoadingView2.hideAllViews();

                // Make sure the opposite end is hidden too
                oppositeListViewLoadingView2.setVisibility(View.GONE);

                // Show the ListView Loading View and set it to refresh.
                listViewLoadingView2.setVisibility(View.VISIBLE);
                listViewLoadingView2.refreshing();
                break;
        }

        if (doScroll) {
            // We need to disable the automatic visibility changes for now
            disableLoadingLayoutVisibilityChanges();

            // We scroll slightly so that the ListView's header/footer is at the
            // same Y position as our normal header/footer
            setHeaderScroll(scrollToY);

            // Make sure the ListView is scrolled to show the loading
            // header/footer
            mRefreshableView.setSelection(selection);

            // Smooth scroll as normal
            smoothScrollTo(0);
        }
    }

    @Override
    protected void onReset() {
        /**
         * If the extras are not enabled, just call up to super and return.
         */
        if (!mListViewExtrasEnabled) {
            super.onReset();
            return;
        }

        final int scrollToHeight, selection;
        final boolean scrollLvToEdge;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                LoadingLayout originalLoadingLayout = getFooterLayout();
                LoadingLayout listViewLoadingLayout = mFooterLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToHeight = getFooterSize();
                scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition()
                        - selection) <= 1;

                // If the ListView header loading layout is showing, then we need to
                // flip so that the original one is showing instead
                if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {

                    // Set our Original View to Visible
                    originalLoadingLayout.showInvisibleViews();

                    // Hide the ListView Header/Footer
                    listViewLoadingLayout.setVisibility(View.GONE);

                    /**
                     * Scroll so the View is at the same Y as the ListView
                     * header/footer, but only scroll if: we've pulled to refresh, it's
                     * positioned correctly
                     */
                    if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
                        mRefreshableView.setSelection(selection);
                        setHeaderScroll(scrollToHeight);
                    }
                }
                break;
            case PULL_FROM_START:
            default:
                LoadingLayout originalLoadingLayout2 = getHeaderLayout();
                LoadingLayout listViewLoadingLayout2 = mHeaderLoadingView;
                scrollToHeight = -getHeaderSize();
                selection = 0;
                scrollLvToEdge = Math.abs(mRefreshableView
                        .getFirstVisiblePosition() - selection) <= 1;

                // If the ListView header loading layout is showing, then we need to
                // flip so that the original one is showing instead
                if (listViewLoadingLayout2.getVisibility() == View.VISIBLE) {

                    // Set our Original View to Visible
                    originalLoadingLayout2.showInvisibleViews();

                    // Hide the ListView Header/Footer
                    listViewLoadingLayout2.setVisibility(View.GONE);

                    /**
                     * Scroll so the View is at the same Y as the ListView
                     * header/footer, but only scroll if: we've pulled to refresh, it's
                     * positioned correctly
                     */
                    if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
                        mRefreshableView.setSelection(selection);
                        setHeaderScroll(scrollToHeight);
                    }
                }
                break;
        }
        // Finally, call up to super
        super.onReset();
    }

    @Override
    protected void handleStyledAttributes(TypedArray a) {
        super.handleStyledAttributes(a);
        mListViewExtrasEnabled = a.getBoolean(
                R.styleable.PullToRefresh_ptrListViewExtrasEnabled, false);
        if (mListViewExtrasEnabled) {
            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL);

            // Create Loading Views ready for use later
            FrameLayout frame = new FrameLayout(getContext());
            mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
            mHeaderLoadingView.setVisibility(View.GONE);
            frame.addView(mHeaderLoadingView, lp);
            mRefreshableView.addHeaderView(frame, null, false);

            mLvFooterLoadingFrame = new FrameLayout(getContext());
            mFooterLoadingView = createLoadingLayout(getContext(),
                    Mode.PULL_FROM_END, a);
            mFooterLoadingView.setVisibility(View.GONE);
            mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

            /**
             * If the value for Scrolling While Refreshing hasn't been
             * explicitly set via XML, enable Scrolling While Refreshing.
             */
            if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
                setScrollingWhileRefreshingEnabled(true);
            }
        }
    }

    protected class InternalListView extends AutoLoadListView implements EmptyViewMethodAccessor {

        private boolean mAddedLvFooter = false;

        public InternalListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                super.dispatchDraw(canvas);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                return super.dispatchTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            // Add the Footer View at the last possible moment
            if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
                addFooterView(mLvFooterLoadingFrame, null, false);
                mAddedLvFooter = true;
            }

            super.setAdapter(adapter);
        }

        @Override
        public void setEmptyView(View emptyView) {
            PullToRefreshAutoLoadListView.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);

        }

    }
}
