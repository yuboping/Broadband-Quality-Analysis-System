package com.asiainfo.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.asiainfo.util.common.Judgment;

public class ToolsUtils {
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0;) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean StringIsNull(String str) {
        if (null == str || "".equals(str))
            return true;
        return false;
    }

    public static boolean ListIsNull(List<?> list) {
        if (null == list || list.isEmpty())
            return true;
        return false;
    }

    public static boolean isIpv4(String ipAddress) {

        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();

    }

    public static boolean isSyslogAddr(String ipAddress) {

        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)" + ":\\d+";

        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();

    }

    public static void main(String[] args) {
        System.out.println(isSyslogAddr("10.10.01.9:wer"));
    }

    /**
     * 将List<String>集合 转化为String 如{"aaa","bbb"} To 'aaa','bbb'
     */
    public static String convertListToString(List<String> strlist) {
        StringBuffer sb = new StringBuffer();
        if (!Judgment.listIsNull(strlist)) {
            for (int i = 0; i < strlist.size(); i++) {
                if (i == 0) {
                    sb.append("'").append(strlist.get(i)).append("'");
                } else {
                    sb.append(",").append("'").append(strlist.get(i)).append("'");
                }
            }
        }
        return sb.toString();
    }
}
