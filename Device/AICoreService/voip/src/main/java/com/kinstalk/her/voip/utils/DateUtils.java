package com.kinstalk.her.voip.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by siqing on 17/10/19.
 */

public class DateUtils {

    public static String getTodayDateStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    public static String getDateStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(date).toString();
    }

    public static boolean isToday(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        if (fmt.format(date).toString().equals(fmt.format(new Date()).toString())) {//格式化为相同格式
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWeekIn(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        String createDate = fmt.format(date);
        String todayDate = fmt.format(new Date());
        if (Long.parseLong(todayDate) - Long.parseLong(createDate) > 7) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取通话记录时间
     * @param time
     * @return
     */
    public static String parseRecordDate(long time) {
        Date date = new Date(time);
        if (DateUtils.isToday(date)) {
            return DateUtils.getTodayDateStr(date);
        } else if (DateUtils.isWeekIn(date)) {
            return DateUtils.getWeekStr(time);
        } else {
            return DateUtils.getDateStr(date);
        }
    }


    public static String getWeekStr(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String weekStr = "周";
        switch (day) {
            case Calendar.SUNDAY:
                return weekStr + "日";
            case Calendar.MONDAY:
                return weekStr + "一";
            case Calendar.TUESDAY:
                return weekStr + "二";
            case Calendar.WEDNESDAY:
                return weekStr + "三";
            case Calendar.THURSDAY:
                return weekStr + "四";
            case Calendar.FRIDAY:
                return weekStr + "五";
            case Calendar.SATURDAY:
                return weekStr + "六";
        }
        return "";
    }

    /**
     * 是否是同一天
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameDay(long time1, long time2) {
        String dateStr1 = getDateStr(new Date(time1));
        String dateStr2 = getDateStr(new Date(time2));
        if (dateStr1.equals(dateStr2)) {
            return true;
        }
        return false;
    }
}
