package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {


    public static String getDateStr(String pattern) {
        return getDateStr(new Date(), pattern);
    }

    public static String getDateStr(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return simpleDateFormat.format(date);
    }

    public static String getWeekDateStr(String date) {
        return date.substring(0, 11) + getCurrentWeekday();
    }

    public static String getMonthDateStr(String date) {
        return date.substring(0, 7);
    }

    /**
     *
     * @return
     */
    public static String getCurrentWeekday() {
        String[] weekStr = new String[]{"", "周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        return weekStr[i];
    }

    /**
     * 获取本周日期list
     */
    private static final int FIRST_DAY = Calendar.MONDAY;

    public static ArrayList<String> getCurrentWeeksDate(String pattern) {
        String[] weekStr = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        Calendar calendar = Calendar.getInstance();
        setToFirstDay(calendar);
        ArrayList<String> date = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            date.add(getDay(calendar, pattern) + " " + weekStr[i]);
            calendar.add(Calendar.DATE, 1);
        }
        return date;
    }

    private static void setToFirstDay(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != FIRST_DAY) {
            calendar.add(Calendar.DATE, -1);
        }
    }

    private static String getDay(Calendar calendar, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取本年每月list(yyyy-MM)
     * @param pattern
     * @return
     */
    public static ArrayList<String> getCurrentMonthsDate(String pattern) {
        String year = getDateStr(pattern);
        ArrayList<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            if (i < 10) months.add(year + "-0" + i);
            else months.add(year + "-" + i);
        }
        return months;
    }

    /**
     * 获取指定时间和当前时间的时间差
     * @param date
     * @return 小于3小时返回相差时间，否则返回日期
     */
    public static String getDateDistance(String date) {
        long distance = new Date().getTime() - getMills(date);
        long minutes = distance / (1000 * 60);
        if (minutes <= 3 * 60) {
            long hours = minutes / 60;
            if (hours < 1) {
                return (int)minutes + "分钟前";
            }else {
                return (int)hours + "小时前";
            }
        } else {
            return date;
        }
    }

    private static long getMills(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d.getTime();
    }
}
