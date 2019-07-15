package com.framework.loippi.utils;

import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.framework.loippi.utils.validator.DateUtils.DEFAULT_FORMAT_STRING;

public class Dateutil {
    public static final String NORMAL_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String HHMM_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String SIMPLE_NORMAL_FORMAT = "yyyy-MM-dd";
    public static final String HHMMDD_FORMAT = "yyMMdd";
    public static final String DEFAULT_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * 取得当前日期是多少周
     *
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 得到某一年周的总数
     *
     * @param year
     * @return
     */
    public static int getMaxWeekNumOfYear(int year) {
        Calendar c = new GregorianCalendar();
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        return getWeekOfYear(c.getTime());
    }

    public static long parseToDateLong(String s, String style) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(style);
        Date date = null;
        if (s == null || s.length() < 5)
            return 0;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 得到某年某周的第一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);
        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, week * 7);
        return getFirstDayOfWeek(cal.getTime());
    }

    /**
     * 字符串转为long型 必须带时、分、秒
     * 例如：2015-09-01对应yyyy-MM-dd
     * 例如：2015-09-01 00:00:00对应yyyy-MM-dd HH:mm:ss
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static long strToLong(String dateStr, String pattern) {
        Date date = toDate(dateStr, pattern);
        return date.getTime();
    }

    /**
     * 将一个字符串转换成日期格式, 字符串类型必须于格式化对应
     * 例如：2015-09-01对应yyyy-MM-dd
     * 例如：2015-09-01 00:00:00对应yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date toDate(String date, String pattern) {
        if (("" + date).equals("")) {
            return null;
        }
        if (pattern == null) {
            pattern = NORMAL_FORMAT;
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(pattern);
        Date newDate = new Date();
        try {
            newDate = sdf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newDate;
    }

    /**
     * 得到某年某周的最后一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getLastDayOfWeek(int year, int week) {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);
        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, week * 7);
        return getLastDayOfWeek(cal.getTime());
    }

    /**
     * 取得指定日期所在周的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c.getTime();
    }

    /**
     * 取得指定日期所在周的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.getTime();
    }

    /**
     * 取得当前日期所在周的第一天
     *
     * @param
     * @return
     */
    public static Date getFirstDayOfWeek() {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() - 1); // Monday
        return c.getTime();
    }

    /**
     * 取得当前日期所在周的最后一天
     *
     * @param
     * @return
     */
    public static Date getLastDayOfWeek() {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.getTime();
    }

    public static String longToStr(Long longDate) {
        return new SimpleDateFormat(SIMPLE_NORMAL_FORMAT).format(longDate);
    }

    public static String longToStr(Long longDate, String format) {
        return new SimpleDateFormat(format).format(longDate);
    }

    //日期（2位/2位/2位）例如170324 日期2017年3月24日
    public static String shortToDate(Date date) {
        return new SimpleDateFormat(HHMMDD_FORMAT).format(date);
    }

    public static String shortToStr(Date date) {
        return new SimpleDateFormat(SIMPLE_NORMAL_FORMAT).format(date);
    }

    /**
     * 获取当前时间之前的时间
     *
     * @param point 要提前的时间点(年,月,日,时,分,秒)
     * @param num   正数要提前的时间量,负数要退后的时间量
     * @return
     * @format 要返回的时间格式, 如:yyyy-MM-dd HH:mm:ss
     */
    public static String getEalierOrLaterDate(Long date, Integer point, int num, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        if (point.equals(Calendar.YEAR)) {
            calendar.add(Calendar.YEAR, num); // 把时间设置为当前时间-1小时，同理，也可以设置其他时间
        } else if (point.equals(Calendar.MONTH)) {
            calendar.add(Calendar.MONTH, num);
        } else if (point.equals(Calendar.DAY_OF_YEAR)) {
            calendar.add(Calendar.DAY_OF_YEAR, num);
        } else if (point.equals(Calendar.HOUR)) {
            calendar.add(Calendar.HOUR, num);
        } else if (point.equals(Calendar.MINUTE)) {
            calendar.add(Calendar.MINUTE, num);
        } else if (point.equals(Calendar.SECOND)) {
            calendar.add(Calendar.SECOND, num);
        }
        return new SimpleDateFormat(format).format(calendar.getTime());// 获取到完整的时间
    }

    /**
     * 按格式把字符串转换为日期
     *
     * @param time
     * @param format
     * @return
     */
    public static Date parseStrFromToDate(String time, String format) {
        if (time == null || time.trim().length() == 0) return null;
        if (format == null || format.trim().length() == 0) format = Dateutil.NORMAL_FORMAT;
        try {
            return new SimpleDateFormat(format).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断日期是不是超过今天
     *
     * @param date
     * @return
     */
    public static boolean isGreaterToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar1.getTime()));
        return calendar.getTimeInMillis() > calendar1.getTimeInMillis();
    }

    /**
     * 获取当天的结束时间
     *
     * @return
     */
    public static Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
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

    //日期格式校验
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static Integer toInt(Object object) {
        if (object == null) {
            return 0;
        }
        return Integer.valueOf(object + "");
    }

    /**
     * Date转string 获取时间yyyyMMddHHmmss 获取当前时间
     *
     * @return String
     */
    public static String getDateString() {
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(DEFAULT_FORMAT);
        String date = df.format(new Date());
        return date;
    }

    /**
     * Date转string 获取时间yyyyMMddHHmmss 获取当前时间
     *
     * @return String
     */
    public static String getShotDateString() {
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(new Date());
        return date;
    }


    /**
     * 获取本周一的日期
     *
     * @return
     */
    public static Date getWeekStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date date = cal.getTime();
        return date;
    }

    /**
     * 获取后一天的日期
     *
     * @return
     */
    public static Date tomorrowDate() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String dateString = df.format(date);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String format(String xFormat) {
        Date date = new Date();
        xFormat = StringUtils.isNotEmpty(xFormat) ? xFormat : "yyyy-MM-dd HH:mm:ss";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(xFormat);
        return sdf.format(date);
    }

    public static String now() {
        Date date = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String getFormatDate(Date date, String xFormat) {
        date = date == null ? new Date() : date;
        xFormat = StringUtils.isNotEmpty(xFormat) ? xFormat : "yyyy-MM-dd HH:mm:ss";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(xFormat);
        return sdf.format(date);
    }

    public static int compareDate(Date dateX, Date dateY) {
        return dateX.compareTo(dateY);
    }

    public static Date parseString2Date(String xDate, String xFormat) {
        if (!isNotDate(xDate)) {
            xFormat = StringUtils.isNotEmpty(xFormat) ? xFormat : "yyyy-MM-dd";
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(xFormat);
            Date date = null;

            try {
                date = sdf.parse(xDate);
                return date;
            } catch (ParseException var5) {
                var5.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static boolean isNotDate(String xDate) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        try {
            if (StringUtils.isEmpty(xDate)) {
                return true;
            } else {
                sdf.parse(xDate);
                return false;
            }
        } catch (ParseException var3) {
            var3.printStackTrace();
            return true;
        }
    }

    public static boolean isDate(String xDate) {
        return !isDate(xDate);
    }

    public static int getDiffDays(Date dateX, Date dateY) {
        if (dateX != null && dateY != null) {
            int dayX = (int) (dateX.getTime() / 86400000L);
            int dayY = (int) (dateY.getTime() / 86400000L);
            return dayX > dayY ? dayX - dayY : dayY - dayX;
        } else {
            return 0;
        }
    }

    public static String getAfterCountDate(Date date, int after, String xFormat) {
        date = date == null ? new Date() : date;
        xFormat = StringUtils.isNotEmpty(xFormat) ? xFormat : "yyyy-MM-dd";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, after);
        return getFormatDate(calendar.getTime(), xFormat);
    }

    public static int getDateTimeParam(Object xDate, String xFormat) {
        xDate = xDate == null ? new Date() : xDate;
        Date date = null;
        if (xDate instanceof String) {
            date = parseString2Date(xDate.toString(), (String) null);
        } else if (xDate instanceof Date) {
            date = (Date) xDate;
        } else {
            date = new Date();
        }

        date = date == null ? new Date() : date;
        if (!StringUtils.isNotEmpty(xFormat) || !xFormat.equals("yyyy") && !xFormat.equals("MM") && !xFormat.equals("dd")) {
            if (StringUtils.isNotEmpty(xFormat) && "week".equals(xFormat)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int week = cal.get(7) - 1 == 0 ? 7 : cal.get(7) - 1;
                return week;
            } else {
                return 0;
            }
        } else {
            return Integer.parseInt(getFormatDate(date, xFormat));
        }
    }

    public static String getWeekString(Object xDate) {
        int week = getDateTimeParam(xDate, "week");
        switch (week) {
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 7:
                return "星期日";
            default:
                return "";
        }
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    public static Calendar getCalendar(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(millis));
        return cal;
    }

    public static String getDataString(java.text.SimpleDateFormat formatstr) {
        return formatstr.format(getCalendar().getTime());
    }

    public static String getDataString(String formatStr) {
        java.text.SimpleDateFormat sdFormat = new java.text.SimpleDateFormat(formatStr);
        return getDataString(sdFormat);
    }

    public static String getFormatDate1(Date date, String xFormat) {
        if (date == null) {
            return "";
        } else {
            xFormat = StringUtils.isNotEmpty(xFormat) ? xFormat : "yyyy-MM-dd HH:mm:ss";
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(xFormat);
            return sdf.format(date);
        }
    }

    public static String getFormatDateEn(String date, String xFormat) {
        if (date != null && !"".equals(date)) {
            xFormat = StringUtils.isNotEmpty(xFormat) ? xFormat : "yyyy-MM-dd";
            Date parseString2Date = parseString2Date(date, xFormat);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("ddE-MMMMM, yyyy", Locale.ENGLISH);
            return sdf.format(parseString2Date);
        } else {
            return "";
        }
    }

    public static Date addDateOneDay(Date date) {
        if (date == null) {
            return date;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(6, 1);
            date = c.getTime();
            return date;
        }
    }

    public static String addDateOneDay(String dateStr) {
        if (dateStr == null) {
            return null;
        } else {
            Date date = parseString2Date(dateStr, "yyyy-MM-dd");
            if (date == null) {
                return null;
            } else {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(6, 1);
                date = c.getTime();
                return getFormatDate(date, "yyyy-MM-dd");
            }
        }
    }

    public static String decDateOneDay(String dateStr) {
        if (dateStr == null) {
            return null;
        } else {
            Date date = parseString2Date(dateStr, "yyyy-MM-dd");
            if (date == null) {
                return null;
            } else {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(6, -1);
                date = c.getTime();
                return getFormatDate(date, "yyyy-MM-dd");
            }
        }
    }

    public static String calStrmp(Date create) {
        if (create == null) {
            return "";
        }
        Long stremp = (System.currentTimeMillis() - create.getTime()) / 1000;
        if (stremp <= 60) {
            return Long.toString(stremp) + "s前";
        }
        Long min = stremp / 60;
        if (min <= 60) {
            return Long.toString(min) + "min前";
        }
        Long hours = min / 60;
        if (hours <= 24) {
            return Long.toString(hours) + "h前";
        }
        Long day = hours / 24;
        return day + "天前";
    }

    public class DateFormat {
        public static final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static final String TIME_FORMAT = "HH:mm:ss";
        public static final String FULL_DATE_FORMAT_CN = "yyyy年MM月dd日 HH时mm分ss秒";
        public static final String PART_DATE_FORMAT = "yyyy-MM-dd";
        public static final String PART_DATE_FORMAT_SHORT = "yyyyMMdd";
        public static final String PART_DATE_FORMAT_LONG = "yyyyMMddHHmmss";
        public static final String PART_DATE_FORMAT_CN = "yyyy年MM月dd日";
        public static final String SHORT_DATE_FORMAT_CN = "yyyy年MM月";
        public static final String PART_DATE_FORMAT_EN = "ddE-MMMMM, yyyy";
        public static final String YEAR_DATE_FORMAT = "yyyy";
        public static final String MONTH_DATE_FORMAT = "MM";
        public static final String DAY_DATE_FORMAT = "dd";
        public static final String WEEK_DATE_FORMAT = "week";
        public static final String FULL_DATE_FORMAT_SS = "yyyyMMddHHmmssSS";

        public DateFormat() {
        }
    }

}
