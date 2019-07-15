package com.framework.loippi.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by longbh on 2016/10/20.
 */
public class WeekUtils {

    private static final long ONE_MINUTE = 60;
    private static final long ONE_HOUR = 3600;
    private static final long ONE_DAY = 86400;
    private static final long ONE_MONTH = 2592000;
    private static final long ONE_YEAR = 31104000;

    private static String[] weekStr = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    public static String format(String weeksIdx) {
        if (hashAll(weeksIdx)) {
            return "每天";
        }
        if (hashSleep(weeksIdx)) {
            return "周末";
        }
        if (hashWork(weeksIdx)) {
            return "工作日";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < 8; i++) {
            if (weeksIdx.indexOf("" + i) > -1) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(weekStr[i - 1]);
            }
        }

        return stringBuilder.toString();
    }

    public static boolean contains(String week1, String week2) {
        for (int i = 1; i < 8; i++) {
            if (week1.indexOf("" + i) >= 0 && week2.indexOf("" + i) >= 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean hashAll(String weekArray) {
        for (int i = 1; i < 8; i++) {
            if (weekArray.indexOf("" + i) < 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean hashSleep(String weekArray) {
        for (int i = 6; i < 8; i++) {
            if (weekArray.indexOf("" + i) < 0) {
                return false;
            }
        }

        for (int i = 1; i < 6; i++) {
            if (weekArray.indexOf("" + i) > -1) {
                return false;
            }
        }

        return true;
    }

    private static boolean hashWork(String weekArray) {
        for (int i = 1; i < 6; i++) {
            if (weekArray.indexOf("" + i) < 0) {
                return false;
            }
        }

        for (int i = 6; i < 8; i++) {
            if (weekArray.indexOf("" + i) > -1) {
                return false;
            }
        }

        return true;
    }

    public static Date dayPree(int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        c.add(Calendar.DATE, day);

        return c.getTime();
    }

    public static Long dayPreeMini(int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.DATE, day);

        return c.getTimeInMillis();
    }

    /**
     * 距离今天的绝对时间
     *
     * @param date
     * @return
     */
    public static String toToday(Date date) {
        long time = date.getTime() / 1000;
        long now = dayPreeMini(0) / 1000;
        long last = dayPreeMini(-1) / 1000;
        if (time <= now)
            return "今天 " + Dateutil.getFormatDate(date, "HH:mm");
        else if (time <= last)
            return "昨天 " + Dateutil.getFormatDate(date, "HH:mm");
        else {
            return Dateutil.getFormatDate(date, "yyyy-MM-dd HH:mm");
        }
    }

    //时间判断

    public static String parseDate(Date date) {
        Long create = date.getTime();
        Calendar now = Calendar.getInstance();
        long ms = 1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND));//毫秒数
        long ms_now = now.getTimeInMillis();
        if (ms_now - create < ms) {
            return "今天 " + Dateutil.getFormatDate(date, "HH:mm");
        } else if (ms_now - create < (ms + 24 * 3600 * 1000)) {
            return "昨天 " + Dateutil.getFormatDate(date, "HH:mm");
        } else {
            return Dateutil.getFormatDate(date, "yyyy-MM-dd HH:mm");
        }
    }


}
