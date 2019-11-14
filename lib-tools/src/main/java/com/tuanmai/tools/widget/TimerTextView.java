package com.tuanmai.tools.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tuanmai.tools.Utils.DateUtil;

public class TimerTextView extends TextView {

    /**
     * 当前值
     */
    private long currentCount;
    /**
     * 倒计时的线程
     */
    private TimeCount time;
    /**
     * 倒计时结束监听
     */
    private CountDownFinishListener mCountDownFinishListener;

    public TimerTextView(Context context) {
        super(context);
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /***
     * 设置当前值
     *
     * @param currentCount
     */
    public void setCurrentCount(long currentCount) {
        this.currentCount = currentCount;
    }

    /**
     * 构造CountDownTimer对象并开始倒计时 当倒计时的时间已经满了，则不再启动该线程
     */
    public void start() {
        if (time == null) {
            // 由于timer计时器的影响，只能计数到1，所以这边需要+1秒，到后面显示时，就-1秒显示即可。
            time = new TimeCount((currentCount + 1000), 1000);
        } else {
            time.cancel();
            time = new TimeCount((currentCount + 1000), 1000);
        }
        time.start();
    }

    /**
     * 停止倒计时的线程
     */
    public void stop() {
        if (time != null) {
            time.cancel();
            time = null;
        }
        setEnabled(true);
    }

    /**
     * 倒计时结束时的回调接口
     */
    public interface CountDownFinishListener {
        /**
         * 倒计时结束时调用该方法
         */
        void onCountDownFinish();
    }

    /**
     * 设置倒计时结束时的监听
     */
    public void setOnCountDownFinishListener(CountDownFinishListener l) {
        mCountDownFinishListener = l;
    }

    /* 定义一个倒计时的内部类 */
    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            setEnabled(false);
        }

        @Override
        public void onFinish() {
            setEnabled(true);
            if (mCountDownFinishListener != null) {
                mCountDownFinishListener.onCountDownFinish();
            }
            // 计时完毕时触发，但稍微有两秒钟的延迟，不推荐使用
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 计时过程显示，由于之前多添加了一秒，现在这里减去一秒显示
            // showText = txtContent + " " +
            // DateUtil.dateBetween(millisUntilFinished - 1000);
            currentCount = millisUntilFinished - 1000;

            setText(DateUtil.dateBetween(currentCount, DateUtil.TimeType.TYPE_SECOND));
        }
    }
}
