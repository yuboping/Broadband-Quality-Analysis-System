package com.asiainfo.util.page;

import org.springframework.util.StringUtils;

import java.text.MessageFormat;

/**
 * 
 * @ClassName: PagingUtil
 * @Description: TODO(跨Oracle、MySQL通用分页查询)
 * @author yubp@asiainfo-sec.com
 * @date 2018年7月25日 上午10:29:23
 *
 */
public class PagingUtil {
    public static final String MYSQL = "mysql";
    public static final String ORACLE = "oracle";
    private static String databaseType = ORACLE;

    /**
     * 获取数据库类型
     * 
     * @return
     */
    public static String getDatabaseType() {
        return databaseType;
    }

    /**
     * 获取通用于MySQL和Oracle数据库的分页语句
     * 
     * @param sql
     *            需要分页的sql语句，参数采用{0}，{1}这样的方式，与params数组的数据一一对应
     * @param page
     * @return
     */
    public static String getPageSql(String sql, Page page) {
        return getPageSql(sql, null, page);
    }

    /**
     * 获取通用于MySQL和Oracle数据库的分页语句
     * 
     * @param sql
     *            需要分页的sql语句，参数采用{0}，{1}这样的方式，与params数组的数据一一对应
     * @param params
     *            参数数组
     * @param page
     * @return
     */
    public static String getPageSql(String sql, Object[] params, Page page) {
        if (StringUtils.isEmpty(sql)) {
            return null;
        }
        /**
         * 根据数据库类型不同调用不同的方法
         */
        String databaseType = getDatabaseType();
        if (MYSQL.equals(databaseType)) {
            return getMysqlSql(sql, params, page);
        } else if (ORACLE.equals(databaseType)) {
            return getOracleSql(sql, params, page);
        }
        return null;
    }

    /**
     * 获取通用于MySQL和Oracle数据库的分页语句
     * 
     * @param sql
     *            sql语句
     * @return 统计数量取字段：CNT
     */
    public static String getCountSql(String sql) {
        return getCountSql(sql, null);
    }

    /**
     * 获取通用于MySQL和Oracle数据库的分页语句
     * 
     * @param sql
     *            sql语句，参数采用{0}，{1}这样的方式，与params数组的数据一一对应
     * @param params
     *            参数数组
     * @return 统计数量取字段：CNT
     */
    public static String getCountSql(String sql, Object[] params) {
        if (StringUtils.isEmpty(sql)) {
            return null;
        }
        String upperSql = sql.toUpperCase();
        if (upperSql.indexOf(" FROM ") == -1) {
            return null;
        }
        /**
         * 格式化参数
         */
        if (params != null && params.length > 0) {
            MessageFormat messageFormat = new MessageFormat(sql);
            sql = messageFormat.format(params);
        }
        int index = upperSql.indexOf(" FROM ");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) CNT ").append(sql.substring(index));
        return sb.toString();
    }

    /**
     * oracle 分页 rownum
     * 
     * @param sql
     *            需要分页的sql语句，参数采用{0}，{1}这样的方式，与params数组的数据一一对应
     * @param params
     *            参数数组
     * @param page
     * @return
     */
    private static String getOracleSql(String sql, Object[] params, Page page) {
        /**
         * 格式化参数
         */
        if (params != null && params.length > 0) {
            MessageFormat messageFormat = new MessageFormat(sql);
            sql = messageFormat.format(params);
        }

        if (page == null)
            return sql;

        StringBuilder sb = new StringBuilder();
        sb.append("select * from (").append("select rownum rn, a.* from(").append(sql)
                .append(") a where rownum <= ").append(page.getPageNumber() * page.getPageSize())
                .append(") where rn > ").append((page.getPageNumber() - 1) * page.getPageSize());
        return sb.toString();
    }

    /**
     * mysql分页 limit
     * 
     * @param sql
     *            需要分页的sql语句，参数采用{0}，{1}这样的方式，与params数组的数据一一对应
     * @param params
     *            参数数组
     * @param page
     * @return
     */
    private static String getMysqlSql(String sql, Object[] params, Page page) {
        /**
         * 格式化参数
         */
        if (params != null && params.length > 0) {
            MessageFormat messageFormat = new MessageFormat(sql);
            sql = messageFormat.format(params);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        if (page != null) {
            sb.append(" LIMIT ");
            sb.append((page.getPageNumber() - 1) * page.getPageSize());
            sb.append(" , ");
            sb.append(page.getPageSize());
        }
        return sb.toString();
    }

    /**
     * 计算总页数
     *
     * @param totals
     *            总记录数
     * @param rows
     *            每页显示数
     * @return
     */
    public static int getPages(Integer totals, Integer rows) {
        if (totals <= 0 || rows <= 0) {
            return 0;
        }
        int pages = totals / rows;
        if (totals % rows > 0) {
            pages++;
        }
        return pages;
    }

}
