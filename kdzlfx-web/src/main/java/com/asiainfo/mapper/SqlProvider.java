package com.asiainfo.mapper;

import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.asiainfo.model.chartData.OltHistory;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.util.common.DbSqlUtil;
import com.asiainfo.util.page.Page;
import com.asiainfo.util.page.PagingUtil;

public class SqlProvider {
    public String getOffnetTendency(Map<String, Object> parameters) {
        String account = (String) parameters.get("account");
        @SuppressWarnings("unchecked")
        List<ParamRelation> months = (List<ParamRelation>) parameters.get("months");
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < months.size(); i++) {
            String month = months.get(i).getValue();
            sql.append("select "
                    + month.substring(0, 6)
                    + " as attr, u.inoctets as inoctets_sum,u.outoctets as outoctets_sum from USER_BILL_"
                    + month.substring(4, 6) + " u where u.username='" + account + "'");
            if (i < months.size() - 1) {
                sql.append(" union all ");
            }
        }
        return sql.toString();
    }

    public String getOffnetTendencyHealth(Map<String, Object> parameters) {
        String account = (String) parameters.get("account");
        @SuppressWarnings("unchecked")
        List<ParamRelation> months = (List<ParamRelation>) parameters.get("months");
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < months.size(); i++) {
            String month = months.get(i).getValue();
            sql.append("select " + month.substring(0, 6)
                    + " as attr, c.prob*100 as off_prob from CES_PREDICT_OFFNET_"
                    + month.substring(4, 6) + " c where c.user_name='" + account + "'");
            if (i < months.size() - 1) {
                sql.append(" union all ");
            }
        }
        return sql.toString();
    }

    /**
     * 查询olt历史告警（分页）
     * @param parameters
     * @return
     */
    public String getOltHistoryList(Map<String, Object> parameters) {
        OltHistory oltHistory = (OltHistory) parameters.get("oltHistory");
        String month = (String) parameters.get("month");
        StringBuffer strb = new StringBuffer();

        strb.append("select * ")
                .append("from (select * ")
                .append("from (select nma.alarm_time, ")
                .append("nma.alarm_dimension1 as oltip, ")
                .append("m.onlinemidnum as mid_value, ")
                .append("nma.onlinenum as now_value, ")
                .append("m.onlinemidnum - nma.onlinenum as down_value, ")
                .append("round(((m.onlinemidnum - nma.onlinenum) / m.onlinemidnum) * 100, 2) as down_per ")
                .append("from (select a.alarm_time, a.alarm_dimension1, o.onlinenum ")
                .append("from CES_ALARM_INFO a ")
                .append("left join OLT_ONLINE_")
                .append(month)
                .append(" o ")
                .append("on (a.alarm_time = o.sta_date and a.alarm_dimension1 = o.oltip) ")
                .append("where a.alarm_name = 'oltUserOnline' ")
                .append("and TRUNC(a.alarm_time) not in (select TRUNC(m.sta_date) from OLT_MID_ONLINE m ")
                .append("where m.oltip = a.alarm_dimension1) ")
                .append("order by a.alarm_time desc) nma ")
                .append("left join (select * from (select oltip, sta_date, onlinemidnum, row_number() over(partition by oltip order by sta_date desc) as rn ")
                .append("from OLT_MID_ONLINE) where rn = 1) m on (m.oltip = nma.alarm_dimension1)) where mid_value > 1000 ")
                .append("union all ")
                .append("select * from (select a.alarm_time, a.alarm_dimension1 as oltip, m.onlinemidnum as mid_value, ")
                .append("o.onlinenum as now_value, m.onlinemidnum - o.onlinenum as down_value, ")
                .append("round(((m.onlinemidnum - o.onlinenum) / m.onlinemidnum) * 100, 2) as down_per from CES_ALARM_INFO a ")
                .append("left join OLT_MID_ONLINE m on (m.oltip = a.alarm_dimension1 and TRUNC(a.alarm_time) = TRUNC(m.sta_date)) ")
                .append("left join OLT_ONLINE_")
                .append(month)
                .append(" o on (a.alarm_time = o.sta_date and a.alarm_dimension1 = o.oltip) ")
                .append("where a.alarm_name = 'oltUserOnline' and m.onlinemidnum > 1000)) where 1=1 ");

        if (!StringUtils.isEmpty(oltHistory.getOlt_ip())) {
            strb.append(" AND oltip = '").append(oltHistory.getOlt_ip()).append("'");
        }
        if (!StringUtils.isEmpty(oltHistory.getStart_time())) {
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + ">='" + oltHistory.getStart_time() + "'");
        }
        if (!StringUtils.isEmpty(oltHistory.getEnd_time())){
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + "<='" + oltHistory.getEnd_time() + "'");
        }

        strb.append("order by alarm_time desc, mid_value desc");


        Page page = (Page) parameters.get("page");
        String sql = strb.toString();
        sql = PagingUtil.getPageSql(sql, page);
        return sql;
    }

    /**
     * 查询olt历史告警（分页）
     * @param parameters
     * @return
     */
    public String getOltHistoryListNew(Map<String, Object> parameters) {
        OltHistory oltHistory = (OltHistory) parameters.get("oltHistory");
        StringBuffer strb = new StringBuffer();

        strb.append("select * from ces_olt_history where 1=1 ");

        if (!StringUtils.isEmpty(oltHistory.getOlt_ip())) {
            strb.append(" AND oltip = '").append(oltHistory.getOlt_ip()).append("'");
        }
        if (!StringUtils.isEmpty(oltHistory.getStart_time())) {
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + ">='" + oltHistory.getStart_time() + "'");
        }
        if (!StringUtils.isEmpty(oltHistory.getEnd_time())){
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + "<='" + oltHistory.getEnd_time() + "'");
        }

        strb.append("order by alarm_time desc, mid_value desc");


        Page page = (Page) parameters.get("page");
        String sql = strb.toString();
        sql = PagingUtil.getPageSql(sql, page);
        return sql;
    }

    /**
     * 查询olt历史告警总条数
     * @param parameters
     * @return
     */
    public String getOltHistoryCount(Map<String, Object> parameters) {
        OltHistory oltHistory = (OltHistory) parameters.get("oltHistory");
        String month = (String) parameters.get("month");
        StringBuffer strb = new StringBuffer();

        strb.append("select count(*) from (SELECT * ")
                .append("from (select * ")
                .append("from (select nma.alarm_time, ")
                .append("nma.alarm_dimension1 as oltip, ")
                .append("m.onlinemidnum as mid_value, ")
                .append("nma.onlinenum as now_value, ")
                .append("m.onlinemidnum - nma.onlinenum as down_value, ")
                .append("round(((m.onlinemidnum - nma.onlinenum) / m.onlinemidnum) * 100, 2) as down_per ")
                .append("from (select a.alarm_time, a.alarm_dimension1, o.onlinenum ")
                .append("from CES_ALARM_INFO a ")
                .append("left join OLT_ONLINE_")
                .append(month)
                .append(" o ")
                .append("on (a.alarm_time = o.sta_date and a.alarm_dimension1 = o.oltip) ")
                .append("where a.alarm_name = 'oltUserOnline' ")
                .append("and TRUNC(a.alarm_time) not in (select TRUNC(m.sta_date) from OLT_MID_ONLINE m ")
                .append("where m.oltip = a.alarm_dimension1) ")
                .append("order by a.alarm_time desc) nma ")
                .append("left join (select * from (select oltip, sta_date, onlinemidnum, row_number() over(partition by oltip order by sta_date desc) as rn ")
                .append("from OLT_MID_ONLINE) where rn = 1) m on (m.oltip = nma.alarm_dimension1)) where mid_value > 1000 ")
                .append("union all ")
                .append("select * from (select a.alarm_time, a.alarm_dimension1 as oltip, m.onlinemidnum as mid_value, ")
                .append("o.onlinenum as now_value, m.onlinemidnum - o.onlinenum as down_value, ")
                .append("round(((m.onlinemidnum - o.onlinenum) / m.onlinemidnum) * 100, 2) as down_per from CES_ALARM_INFO a ")
                .append("left join OLT_MID_ONLINE m on (m.oltip = a.alarm_dimension1 and TRUNC(a.alarm_time) = TRUNC(m.sta_date)) ")
                .append("left join OLT_ONLINE_")
                .append(month)
                .append(" o on (a.alarm_time = o.sta_date and a.alarm_dimension1 = o.oltip) ")
                .append("where a.alarm_name = 'oltUserOnline' and m.onlinemidnum > 1000)) where 1=1 ");

        if (!StringUtils.isEmpty(oltHistory.getOlt_ip())) {
            strb.append(" AND oltip = '").append(oltHistory.getOlt_ip()).append("'");
        }
        if (!StringUtils.isEmpty(oltHistory.getStart_time())) {
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + ">='" + oltHistory.getStart_time() + "'");
        }
        if (!StringUtils.isEmpty(oltHistory.getEnd_time())){
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + "<='" + oltHistory.getEnd_time() + "'");
        }

        strb.append(")");

        return strb.toString();
    }

    /**
     * 查询olt历史告警总条数
     * @param parameters
     * @return
     */
    public String getOltHistoryCountNew(Map<String, Object> parameters) {
        OltHistory oltHistory = (OltHistory) parameters.get("oltHistory");
        StringBuffer strb = new StringBuffer();

        strb.append("select count(*) from ces_olt_history where 1=1 ");

        if (!StringUtils.isEmpty(oltHistory.getOlt_ip())) {
            strb.append(" AND oltip = '").append(oltHistory.getOlt_ip()).append("'");
        }
        if (!StringUtils.isEmpty(oltHistory.getStart_time())) {
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + ">='" + oltHistory.getStart_time() + "'");
        }
        if (!StringUtils.isEmpty(oltHistory.getEnd_time())){
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + "<='" + oltHistory.getEnd_time() + "'");
        }

        return strb.toString();
    }

    /**
     * 查询olt历史告警（不分页）
     * 
     * @param parameters
     * @return
     */
    public String getOltHistoryListNotPage(Map<String, Object> parameters) {
        OltHistory oltHistory = (OltHistory) parameters.get("oltHistory");
        StringBuffer strb = new StringBuffer();

        strb.append("select * from ces_olt_history where 1=1 ");

        if (!StringUtils.isEmpty(oltHistory.getOlt_ip())) {
            strb.append(" AND oltip = '").append(oltHistory.getOlt_ip()).append("'");
        }
        if (!StringUtils.isEmpty(oltHistory.getStart_time())) {
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + ">='"
                    + oltHistory.getStart_time() + "'");
        }
        if (!StringUtils.isEmpty(oltHistory.getEnd_time())) {
            strb.append(" AND " + DbSqlUtil.getDateSql("alarm_time") + "<='"
                    + oltHistory.getEnd_time() + "'");
        }

        strb.append("order by alarm_time desc, mid_value desc");
        return strb.toString();
    }
}
