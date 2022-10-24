package com.asiainfo.util.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DbSqlUtil {
	private static final Logger logger = LoggerFactory.getLogger(DbSqlUtil.class);

	private static class BusinessConf{
		private static String getDbName() {
			return "oracle";
		}
	}

	private static final BusinessConf BUSCONF = new BusinessConf();

	/**
	 * 日期 函数 ： 获取格式 y-M-d 
	 * @return
	 */
	public static String getDateSql(String columnName) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_char("+columnName+",'yyyy-MM-dd')";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "date_format("+columnName+",'%Y-%m-%d')";
		}else{
			sql = "date_format("+columnName+",'%Y-%m-%d')";
		}
		return sql;
	}
	
	/**
	 * 日期 函数 ： 获取格式 y-M-d h:m:s
	 * yyyy-MM-dd hh24:mi:ss
	 * @return
	 */
	public static String getTimeSql(String columnName) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_char("+columnName+",'yyyy-MM-dd hh24:mi:ss')";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "date_format("+columnName+",'%Y-%m-%d %H:%i:%s')";
		}else{
			sql = "date_format("+columnName+",'%Y-%m-%d %H:%i:%s')";
		}
		return sql;
	}

	/**
	 * 日期 函数 ： 获取格式 y-M-d h:m:s
	 * yyyy-MM-dd hh24:mi:ss
	 * @return
	 */
	public static String getDateFormatSql(String columnName) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_date("+columnName+",'yyyy-MM-dd hh24:mi:ss')";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "date_format("+columnName+",'%Y-%m-%d %H:%i:%s')";
		}else{
			sql = "date_format("+columnName+",'%Y-%m-%d %H:%i:%s')";
		}
		return sql;
	}

	//获取当前(上，下)周的日期范围如：...,-1上一周，0本周，1下一周...
	public static Map<String,String> getWeekDays(String formatDate, int i) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String,String> map = new HashMap<>();
		Calendar calendar1 = Calendar.getInstance();
		try{
			calendar1.setTime(sdf.parse(formatDate));

			// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
			calendar1.setFirstDayOfWeek(Calendar.MONDAY);

			// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
			int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
			if (1 == dayOfWeek) {
				calendar1.add(Calendar.DAY_OF_MONTH, -1);
			}

			// 获得当前日期是一个星期的第几天
			int day = calendar1.get(Calendar.DAY_OF_WEEK);

			//获取当前日期前（下）x周同星几的日期
			calendar1.add(Calendar.DATE, 7*i);

			calendar1.add(Calendar.DATE, calendar1.getFirstDayOfWeek() - day);



			String beginDate = sdf.format(calendar1.getTime());
			calendar1.add(Calendar.DATE, 6);

			String endDate = sdf.format(calendar1.getTime());

			map.put("startdate", beginDate);
			map.put("enddate", endDate);


		}catch (Exception e){
			logger.error("Exception:", e);
		}
		return map;
	}

	/**
	 * 日期 函数 ： 获取格式 y-M
	 * yyyy-MM
	 * @return
	 */
	public static String getTimeSqlWithMonth(String columnName) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_char("+columnName+",'yyyy-MM')";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "date_format("+columnName+",'%Y-%m')";
		}else{
			sql = "date_format("+columnName+",'%Y-%m')";
		}
		return sql;
	}

	/**
	 * 日期 函数 ： 获取格式 y-M-d h:m
	 * yyyy-MM-dd hh24:mi:ss
	 * @return
	 */
	public static String getTimeDHMSql(String columnName) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_char("+columnName+",'yyyy-MM-dd hh24:mi')";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "date_format("+columnName+",'%Y-%m-%d %H:%i')";
		}else{
			sql = "date_format("+columnName+",'%Y-%m-%d %H:%i')";
		}
		return sql;
	}
	
	/**
     * 日期 函数 ： 获取格式 y-M-d
     * yyyy-MM-dd
     * @return
     */
    public static String getTimeDSql(String columnName) {
        String sql = "";
        if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
            sql = "to_char("+columnName+",'yyyy-MM-dd')";
        }else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
            sql = "date_format("+columnName+",'%Y-%m-%d')";
        }else{
            sql = "date_format("+columnName+",'%Y-%m-%d')";
        }
        return sql;
    }
	
	/**
	 * 日期 函数 ： h:m
	 * hh24:mi
	 * @return
	 */
	public static String getTimeHMSql(String columnName) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_char("+columnName+",'hh24:mi')";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "date_format("+columnName+",'%H:%i')";
		}else{
			sql = "date_format("+columnName+",'%H:%i')";
		}
		return sql;
	}
	
    /**
     * 日期 函数 ：hh24 hh24
     * 
     * @return
     */
    public static String getTimeHSql(String columnName) {
        String sql = "";
        if (EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())) {
            sql = "to_char(" + columnName + ",'hh24')";
        } else if (EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())) {
            sql = "date_format(" + columnName + ",'%H')";
        } else {
            sql = "date_format(" + columnName + ",'%H')";
        }
        return sql;
    }

    /**
     * 日期 函数 ： m mi
     * 
     * @return
     */
	public static String getTimeMSql(String columnName) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_char("+columnName+",'mi')";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "date_format("+columnName+",'%i')";
		}else{
			sql = "date_format("+columnName+",'%i')";
		}
		return sql;
	}
	
	public static String getDateMethod(){
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "sysdate";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "now()";
		}else{
			sql = "now()";
		}
		return sql;
	}
	
	public static String substrMethod(){
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "substr";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "substring";
		}else{
			sql = "substring";
		}
		return sql;
	}
	
	public static boolean isOracle(){
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			return true;
		}
		return false;
	}
	
	public static boolean isMySql(){
		if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			return true;
		}
		return false;
	}
	
	/**
	 * 日期 函数 ： 获取格式 MMDD
	 * @return
	 */
	public static String getDaySql(String columnName) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_char("+columnName+",'MMDD')";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "date_format("+columnName+",'%m%d')";
		}else{
			sql = "date_format("+columnName+",'MMDD')";
		}
		return sql;
	}
	
	/**
	 * 日期 函数 ： 获取格式 MM
	 * @return
	 */
	public static String getMonthSql(String columnName) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_char("+columnName+",'MM')";
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "date_format("+columnName+",'%m')";
		}else{
			sql = "date_format("+columnName+",'MM')";
		}
		return sql;
	}

	public static String getNullFunction(){
		String sql = "IFNULL";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "NVL";
		}
		return sql;
	}
	
	/**
	 * sql查询特殊字符转义
	 * @param str
	 * @return
	 */
	public static String replaceSpecialStr(String str){
		String gstr = str.replaceAll("%","\\\\%").replaceAll("_","\\\\_");
		return gstr;
	}

	public static String getToDateFormat(){
		String sql = "0";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = "to_date('1970-1-1 8:0:0','yyyy-mm-dd hh24:mi:ss')";
		}
		return sql;
	}

    /**
     * 
     * @Title: getRoundingSql
     * @Description: TODO(获取四舍五入sql)
     * @param @param str
     * @param @param digit
     * @param @return 参数
     * @return String 返回类型
     * @throws
     */
    public static String getRoundingSql(String str, int digit) {
        String sql = "";
        if (EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())) {
            sql = "ROUND(" + str + "," + digit + ") ";
        } else if (EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())) {
            sql = "FORMAT(" + str + "," + digit + ")";
        } else {
            sql = "FORMAT(" + str + "," + digit + ")";
        }
        return sql;
    }

	/**
	 * 拼接字符串
	 * @return
	 */
	public static String getConcat(String columnName1, String desc, String columnName2) {
		String sql = "";
		if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
			sql = columnName1 +" || '" + desc + "' || " + columnName2;
		}else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())){
			sql = "concat( " + columnName1 + " , '" +desc +"' , " + columnName2 + " )";
		}else{
			sql = columnName1 +" || '" + desc + "' || " + columnName2;
		}
		return sql;
	}
	
    public static String getSqlQuery() {
        String sql = "";
        if(EnumUtil.DB_ORACLE.equals(BUSCONF.getDbName())){
            sql = "SELECT 1 FROM DUAL";
        } else if(EnumUtil.DB_MYSQL.equals(BUSCONF.getDbName())) {
            sql = "SELECT 1";
        } else {
            sql = "SELECT 1";
        }
        return sql;
	}
}
