package com.tuanmai.tools.Utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {

    private static final long ONE_MINUTE = 60;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final long ONE_MONTH = 2592000;
    private static final long ONE_YEAR = 31104000;


    /**
     * @return yyyy-mm-dd
     * 2012-12-25
     */
    public static String getDate() {
        return getYear() + "-" + getMonth() + "-" + getDay();
    }

    /**
     * @param format
     * @return yyyy年MM月dd HH:mm MM-dd HH:mm 2012-12-25
     */
    public static String getDate(String format) {
        SimpleDateFormat simple = new SimpleDateFormat(format);
        return simple.format(Calendar.getInstance().getTime());
    }

    /**
     * @return yyyy-MM-dd HH:mm 2012-12-29 23:47
     */
    public static String getCurHourMinute() {
        return getHourMinute(Calendar.getInstance().getTimeInMillis());
    }


    /**
     * 将long时间转化为标准时间
     *
     * @param time
     * @return yyyy-MM-dd HH:mm 2012-12-29 23:47
     */
    public static String getHourMinute(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simple.format(c.getTime());

    }

    /**
     * 中文显示时间
     *
     * @return hh小时||mm分钟||ss秒
     */
    public static String transferLongToTimetext(Long millSec) {
        if (millSec / 1000 / 60 / 60 > 0 && millSec % (1000 * 60 * 60) == 0) {
            return millSec / 1000 / 60 / 60 + "小时";
        } else if (millSec / 1000 / 60 > 0 && millSec % (1000 * 60) == 0) {
            return millSec / 1000 / 60 + "分钟";
        } else {
            return millSec / 1000 + "秒";
        }
    }

    public static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        second = l.intValue() / 1000;

        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return (getTwoLength(hour) + "时" + getTwoLength(minute) + "分" + getTwoLength(second) + "秒");
    }

    private static String getTwoLength(final int data) {
        if (data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }

    public static String formatDateToTime(Long millSec) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millSec);
        SimpleDateFormat simple = new SimpleDateFormat("HH:mm");
        return simple.format(c.getTime());
    }

    /**
     * 倒计时时间
     *
     * @return long
     */
    public static long transferCountDown(Long createTime, Long endTime) {
        return endTime - (new Date().getTime() - createTime);
    }

    /**
     * @return yyyy-MM-dd HH:mm:ss 2012-12-29 23:47:36
     */
    public static String getFullDate() {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simple.format(Calendar.getInstance().getTime());
    }

    /**
     * @param dateFormat 时间格式
     * @param millSec    long型时间
     * @return
     */
    public static String transferLongToDate(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static String timedate(long time) {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simple.format(new Date(time));
    }

    /**
     * yyyy/MM/dd HH:mm:ss
     */
    public static String timetoDate(long time) {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return simple.format(new Date(time));
    }

    /**
     * yyyy-MM-dd
     */
    public static String getDate(long time) {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        return simple.format(new Date(time));
    }

    /**
     * yyyy/MM/dd
     */
    public static String getDate2(long time) {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd");
        return simple.format(new Date(time));
    }

    /**
     * yyyy.MM.dd
     */
    public static String getDate3(long time) {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy.MM.dd");
        return simple.format(new Date(time));
    }

    /**
     * 距离未来日期还有多长时间
     *
     * @param date
     * @return
     */
    public static String getFuturePeriod(Date date) {
        long deadline = date.getTime() / 1000;
        long now = (new Date().getTime()) / 1000;
        long remain = deadline - now;

        StringBuilder builder = new StringBuilder("只剩下");
        if (remain <= ONE_HOUR) {
            builder.append(remain / ONE_MINUTE);
            builder.append("分钟");
            return builder.toString();
        } else if (remain <= ONE_DAY) {
            builder.append(remain / ONE_HOUR);
            builder.append("小时");
            builder.append(remain % ONE_HOUR / ONE_MINUTE);
            builder.append("分钟");
            return builder.toString();
        } else {
            builder.append(remain / ONE_DAY);
            builder.append("天");
            builder.append(remain % ONE_DAY / ONE_HOUR);
            builder.append("小时");
            builder.append(remain % ONE_DAY % ONE_HOUR / ONE_MINUTE);
            builder.append("分钟");
            return builder.toString();
        }

    }


    /**
     * 距离今天多久
     *
     * @param date
     * @return
     */
    public static String getBeforePeriod(Date date) {
        return getBeforePeriod(date.getTime());
    }

    /**
     * 距离今天多久
     *
     * @param time
     * @return
     */
    public static String getBeforePeriod(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        long now = new Date().getTime() / 1000;
        long ago = now - time / 1000;
        if (ago < 0) {
            return "刚刚";
        } else if (ago <= ONE_HOUR)
            return ago / ONE_MINUTE + "分钟前";
        else if (ago <= ONE_DAY)
            return ago / ONE_HOUR + "小时前";
        else if (ago <= ONE_MONTH) {
            return ago / ONE_DAY + "天前";
        } else if (ago <= ONE_YEAR) {
            return ago / ONE_MONTH + "个月前";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(ago / ONE_YEAR);
            builder.append("年");
            builder.append(c.get(Calendar.MONTH) + 1);
            builder.append("月");
            builder.append(c.get(Calendar.DATE));
            builder.append("日以前");
            return builder.toString();
        }

    }


    public static String getYear() {
        return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    }

    public static String getMonth() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return String.valueOf(month);
    }

    public static String getFullMonth() {
        return formatDate(Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    public static String getDay() {
        return String.valueOf(Calendar.getInstance().get(Calendar.DATE));
    }

    public static String getFullDay() {
        return formatDate(Calendar.getInstance().get(Calendar.DATE));
    }

    public static String get24Hour() {
        return String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    }

    public static String getMinute() {
        return String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
    }

    public static String getSecond() {
        return String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
    }

    /**
     * @param str 格式 "yyyy-MM-dd HH:mm:ss"
     * @return 时间 long
     * @date 2015-5-21 下午4:43:35
     * @author 刘洲
     * @Description: 字符串转化为时间戳
     */
    public static long getStrToLong(String str) {
        if (TextUtils.isEmpty(str)) {
            return -1;
        }
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 两个时间的差值，返回格式为HH:mm:ss
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String dateBetween(long startTime, long endTime) {
        return dateBetween(endTime - startTime);
    }

    /**
     * 两个时间的差值，返回格式为HH:mm:ss
     *
     * @param diff
     * @return
     */
    public static String dateBetween(long diff) {
        return dateBetween(diff, TimeType.TYPE_SYMBOL);
    }

    public enum TimeType {
        /**
         * 00时00分00秒
         */
        TYPE_CHINESE,
        /**
         * 00时00分00秒(没小时和分钟不会显示)
         */
        TYPE_CHINESE_SPECIAL,
        /**
         * 00:00:00
         */
        TYPE_SYMBOL,
        /**
         * 时分秒 html字体
         */
        TYPE_CHINESE_HTML,
        /**
         * 00秒
         */
        TYPE_SECOND;
    }

    /**
     * 格式化时间，返回格式为HH:mm:ss
     *
     * @param diff 需要转换的时间戳
     * @param type 设置中间分隔显示类型
     * @return
     */
    public static String dateBetween(long diff, TimeType type) {
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long day = diff / nd;// 计算差多少天
        long hour = diff % nd / nh + day * 24;// 计算差多少小时
        long min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
        long sec = diff % nd % nh % nm / ns;// 计算差多少秒

        if (type == TimeType.TYPE_CHINESE_HTML) {
            return setHtmlText(formatDate(hour),
                    formatDate(min - day * 24 * 60), formatDate(sec));

        } else if (type == TimeType.TYPE_SYMBOL) {
            return formatDate(hour) + ":" + formatDate(min - day * 24 * 60) + ":" + formatDate(sec);

        } else if (type == TimeType.TYPE_CHINESE) {
            return String.format("%s时%s分%s秒", formatDate(hour), formatDate(min
                    - day * 24 * 60), formatDate(sec));
        } else if (type == TimeType.TYPE_CHINESE_SPECIAL) {
            if (hour > 0) {
                return dateBetween(diff, TimeType.TYPE_CHINESE);
            } else if (min - day * 24 * 60 > 0) {
                return String.format("%s分%s秒", formatDate(min - day * 24 * 60), formatDate(sec));
            } else {
                return dateBetween(diff, TimeType.TYPE_SECOND);
            }
        } else if (type == TimeType.TYPE_SECOND) {
            return String.format("%s秒", formatDate(diff / ns));
        } else {
            return formatDate(hour) + "-" + formatDate(min - day * 24 * 60) + "-" + formatDate(sec);
        }
    }

    /**
     * 格式化时间文本 -----不同顏色文本
     *
     * @param hour   小时
     * @param minute 分钟
     * @param sec    秒
     * @date 2015-9-22 下午3:59:58
     * @author 刘洲
     * @Description:
     */
    public static String setHtmlText(String hour, String minute, String sec) {
        String text = String
                .format("<font color=\'#f02387\'>%s</font><font color=\'#858585\'>时</font><font color=\'#f02387\'>%s</font><font color=\'#858585\'>分</font><font color=\'#f02387\'>%s</font><font color=\'#858585\'>秒</font>",
                        hour, minute, sec);
        return text;
    }

    /**
     * 格式化时间，也就是当小于9时，自动添加一个0，成为两位数
     *
     * @param target
     * @return
     */
    private static String formatDate(long target) {
        return target < 10 ? ("0" + target) : String.valueOf(target);
    }

    /**
     * 判断日期是今天还是明天
     *
     * @return 0：今天；1：明天；其他：其他日期
     */
    public static int isTodayOrTomorrow(Long time) {
        if (time == null) {
            return -1;
        }
        if (time > 0) {
            long aDay = 24 * 60 * 60 * 1000;
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            long diff = calendar.getTimeInMillis() - time;
            if (diff >= 0 && diff < aDay) {
                //今天
                return 0;
            }
            if (diff > aDay && diff < 2 * aDay) {
                //明天
                return 1;
            }
        }
        return -1;
    }

    /**
     * 判断日期是不是今天
     *
     * @return true：今天；false：不是今天
     */
    public static boolean isToday(Long time) {
        if (time == null) {
            return false;
        }
        if (time > 0) {
            long aDay = 24 * 60 * 60 * 1000;
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            long diff = calendar.getTimeInMillis() - time;
            if (diff >= 0 && diff < aDay) {
                //今天
                return true;
            }
        }
        return false;
    }

    /**
     * 判断日期是不是昨天
     *
     * @return true：今天；false：不是今天
     */
    public static boolean isYesterday(Long time) {
        if (time == null) {
            return false;
        }
        if (time > 0) {
            long aDay = 24 * 60 * 60 * 1000;
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            long diff = calendar.getTimeInMillis() - time;
            if (diff >= aDay && diff < 2 * aDay) {
                //昨天
                return true;
            }
        }
        return false;
    }


    /**
     * 获取星期
     *
     * @param time
     * @return
     */
    public static String getWeek(long time) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(time));
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return "周日";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return "周一";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            return "周二";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            return "周三";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            return "周四";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            return "周五";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return "周六";
        } else {
            return "";
        }
    }

    /**
     * 获取英文的月份
     *
     * @param time
     * @return
     */
    public static String getEnglishMonth(long time) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(time));
        int month = c.get(Calendar.MONTH);
        if (month == Calendar.FEBRUARY) {
            return "February";
        } else if (month == Calendar.JANUARY) {
            return "January";
        } else if (month == Calendar.MARCH) {
            return "March";
        } else if (month == Calendar.APRIL) {
            return "April";
        } else if (month == Calendar.MAY) {
            return "May";
        } else if (month == Calendar.JUNE) {
            return "June";
        } else if (month == Calendar.JULY) {
            return "July";
        } else if (month == Calendar.AUGUST) {
            return "August";
        } else if (month == Calendar.SEPTEMBER) {
            return "September";
        } else if (month == Calendar.OCTOBER) {
            return "October";
        } else if (month == Calendar.NOVEMBER) {
            return "November";
        } else if (month == Calendar.DECEMBER) {
            return "December";
        } else if (month == Calendar.UNDECIMBER) {
            return "Undecimber";
        } else {
            return "";
        }
    }

    /**
     * 获取数字月份
     */
    public static int getMonth(long time) {
        return new Date(time).getMonth();
    }

    private static final String[] MONTH = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

    /**
     * 获取月份简写
     */
    public static String getMonthLogogram(long time) {
        int index = getMonth(time);
        if (index >= 0 && index < MONTH.length) {
            return MONTH[index];
        }
        return "";
    }

    /**
     * 将时间转换为时间戳
     */
    public static Long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }
}