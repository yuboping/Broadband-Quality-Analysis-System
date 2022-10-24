package com.asiainfo.util.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.util.ToolsUtils;

/**
 * 时间格式转换 判断时间是否交叉
 * 
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * long型日期转成格式化字符串
     * 
     * @param date
     * @param format
     * @return
     */
    public static String long2str(long date, String format) {
        LocalDateTime now = LocalDateTime.ofEpochSecond(date / 1000, 0, ZoneOffset.ofHours(8));
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return now.format(df);
    }

    /**
     * string型日期根据对应格式转成long，单位毫秒
     * 
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static long str2long(String date, String format) {
        return str2secondlong(date, format) * 1000;
    }

    /**
     * string型日期根据对应格式转成long时间戳，单位秒
     * 
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static long str2secondlong(String date, String format) {
        if (format.indexOf("hh") < 0 && format.indexOf("HH") < 0) {// 日期格式包含时分秒
            date += " 00:00:00";
            format += " HH:mm:ss";
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        LocalDateTime time = LocalDateTime.parse(date, df);
        return time.toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 根据格式获取当前时间字符串
     * 
     * @param format
     * @return
     */
    public static String getDate(String format) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return now.format(df);
    }

    /**
     * 获取当前时间的前N天
     * 
     * @param format
     * @return
     */
    public static String getMinusDays(String format, int N) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return now.minusDays(N).format(df);
    }

    /**
     * 获取当前时间的前N月
     * 
     * @param format
     * @return
     */
    public static String getMinusMonths(String format, int N) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return now.minusMonths(N).format(df);
    }

    /**
     * 获取当前时间的前N月
     * 
     * @param format
     * @return
     */
    public static String getMinusMonthsTime(String format, int N, String time) {
        LocalDateTime now = LocalDateTime.parse(time,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return now.minusMonths(N).format(df);
    }

    /**
     * 
     * 把毫秒数转换为标准日期时间字符串
     *
     */
    public static String longMilliToStr(long milliSeconds, String format) {
        ZoneId z = ZoneId.systemDefault();
        Instant instant = Instant.now();
        LocalDateTime datetime = LocalDateTime.ofEpochSecond(milliSeconds / 1000, 0,
                z.getRules().getOffset(instant));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return datetime.format(formatter);
    }

    /**
     * 将日期字符串转换成毫秒值
     *
     */
    public static long str2longMill(String date, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        LocalDateTime time = LocalDateTime.parse(date, df);
        return time.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 获取当前时间字符串
     * 
     * @return
     */
    public static String getDate() {
        return getDate(DateUtil.DEFAULT_FORMAT);
    }

    /**
     * 判断开始时间和结束时间是否在同一天
     * 
     * @param starttimeL
     *            毫秒
     * @param endtimeL
     *            毫秒
     * @return
     */
    public static int isOverNight(Long starttimeL, Long endtimeL) {
        LocalDateTime start = LocalDateTime.ofEpochSecond(starttimeL / 1000, 0,
                ZoneOffset.ofHours(8));
        LocalDateTime end = LocalDateTime.ofEpochSecond(endtimeL / 1000, 0, ZoneOffset.ofHours(8));
        if (end.getDayOfYear() != start.getDayOfYear()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 检查字符串是否是符合格式要求的时间
     * 
     * @param time
     * @param format
     * @return
     */
    public static boolean checkTime(String time, String format) {
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
            LocalDateTime.parse(time, df);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Date型转string
     * 
     * @param date
     * @return
     */
    public static String date2String(Date date) {
        return long2str(date.getTime(), DEFAULT_FORMAT);
    }

    /**
     * string型转Date
     * 
     * @param date
     * @return
     */
    public static Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FORMAT);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            logger.error("DateUtil StrToDate errorMessage:" + e.getMessage());
        }
        return date;
    }

    /**
     * 获取往前或往后推 pre 个月份值，返回MM格式
     * 
     * @param pre
     * @return
     */
    public static String getMonth(int pre) {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusMonths(pre);
        return dateTime.toString("MM");
    }

    /**
     * 
     * @Title: stringChangeTimeStamp @Description:
     *         TODO(日期格式字符串转换成时间戳) @param @param timeString @param @param
     *         format @param @return @param @throws Exception 参数 @return Long
     *         返回类型 @throws
     */
    public static Long stringChangeTimeStamp(String timeString, String format) {
        Long date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(timeString).getTime() / 1000L;
        } catch (ParseException e) {
            logger.error("DateUtil stringChangeTimeStamp errorMessage:" + e.getMessage());
        }
        return date;
    }

    /**
     * 
     * @Title: timeStampChangeString @Description:
     *         TODO(时间戳转换成日期格式字符串) @param @param time @param @param
     *         format @param @return 参数 @return String 返回类型 @throws
     */
    public static String timeStampChangeString(long time, String format) {
        String seconds = String.valueOf(time);
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = DEFAULT_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 
     * @Title: isEffectiveDate @Description: TODO(判断当前时间是否在(startTime,
     *         endTime]区间，注意时间格式要一致) @param @param nowTime @param @param
     *         startTime @param @param endTime @param @return 参数 @return boolean
     *         返回类型 @throws
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == endTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @Title: getCutTimeStamp @Description: TODO(获取分割时间段) @param @param
     *         date @param @param auto_date @param @param
     *         time_stamp_difference @param @return 参数 @return List<String>
     *         返回类型 @throws
     */
    public static List<String> getCutTimeStamp(Date date, int auto_date,
            long time_stamp_difference) {
        List<String> resultList = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        long min = calendar.get(Calendar.MINUTE);
        long coefficient = time_stamp_difference / 60;
        long minOrder = (min / coefficient) * coefficient;
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        int minOrderint = (int) minOrder;
        calendar.add(Calendar.MINUTE, minOrderint);
        Date dateEnd = calendar.getTime();
        calendar.add(Calendar.DATE, -(auto_date));
        Date dateStart = calendar.getTime();
        while (dateStart.compareTo(dateEnd) < 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(dateStart);
            int time_stamp_difference_int = (int) time_stamp_difference;
            c.add(Calendar.SECOND, time_stamp_difference_int);
            dateStart = c.getTime();
            resultList.add(date2String(dateStart));
        }
        return resultList;
    }

    public static List<String> getCutTimeStampByFrequency(Date date, int frequency,
            long time_stamp_difference) {
        List<String> resultList = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        long min = calendar.get(Calendar.MINUTE);
        long coefficient = time_stamp_difference / 60;
        long minOrder = (min / coefficient) * coefficient;
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        int minOrderint = (int) minOrder;
        calendar.add(Calendar.MINUTE, minOrderint);
        Date dateEnd = calendar.getTime();
        calendar.add(Calendar.SECOND, -((int) time_stamp_difference * frequency));
        Date dateStart = calendar.getTime();
        while (dateStart.compareTo(dateEnd) < 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(dateStart);
            int time_stamp_difference_int = (int) time_stamp_difference;
            c.add(Calendar.SECOND, time_stamp_difference_int);
            dateStart = c.getTime();
            resultList.add(date2String(dateStart));
        }
        return resultList;
    }

    /**
     * 
     * @Title: getCutTimeStamp @Description: TODO(获取分割时间段) @param @param
     *         date @param @param auto_date @param @param
     *         time_stamp_difference @param @return 参数 @return List<String>
     *         返回类型 @throws
     */
    public static List<String> getCutTimeStamp(String beginTime, String endTime,
            long time_stamp_difference) {
        List<String> resultList = new ArrayList<String>();
        Date dateStart = setTimeOrder(StrToDate(beginTime), time_stamp_difference);
        Date dateEnd = setTimeOrder(StrToDate(endTime), time_stamp_difference);
        while (dateStart.compareTo(dateEnd) < 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(dateStart);
            int time_stamp_difference_int = (int) time_stamp_difference;
            c.add(Calendar.SECOND, time_stamp_difference_int);
            dateStart = c.getTime();
            resultList.add(date2String(dateStart));
        }
        return resultList;
    }

    public static Date setTimeOrder(Date time, long time_stamp_difference) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        long min = calendar.get(Calendar.MINUTE);
        long coefficient = time_stamp_difference / 60;
        long minOrder = (min / coefficient) * coefficient;
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        int minOrderint = (int) minOrder;
        calendar.add(Calendar.MINUTE, minOrderint);
        return calendar.getTime();
    }

    public static void main(String[] args) {
        // String s1 = Password.encryptPassword(
        // "=" + Password.encryptPassword("Gs1cl#bigdata", PasswordType.BASE64)
        // + "\n=",
        // PasswordType.BASE64);
        // System.out.println(s1);
        //
        // System.out.println(Password.decryptPassword("PWMyUmhhV05sY3c9PQo9",
        // 6));
        List<String> resultList = getCutTimeStampByFrequency(StrToDate("2020-01-13 15:00:00"), 3,
                600);
        // List<String> resultList = getCutTimeStamp("2020-01-12 15:00:00",
        // "2020-01-13 15:00:00",
        // 600);
        String s = ToolsUtils.convertListToString(resultList);
        System.out.println("end");
    }
}
