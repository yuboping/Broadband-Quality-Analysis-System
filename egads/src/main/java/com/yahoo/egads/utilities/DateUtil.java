package com.yahoo.egads.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
            e.printStackTrace();
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

}
