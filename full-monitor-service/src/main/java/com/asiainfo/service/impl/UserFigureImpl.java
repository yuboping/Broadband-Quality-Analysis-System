package com.asiainfo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.mapper.CesBpppBehaviorCharMapper;
import com.asiainfo.mapper.CesBpppComplaintCharMapper;
import com.asiainfo.mapper.CesBpppLinenoCharMapper;
import com.asiainfo.mapper.CesBpppPcCharMapper;
import com.asiainfo.mapper.PsProvHistoryMapper;
import com.asiainfo.mapper.TerminalMapper;
import com.asiainfo.mapper.UserBillMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chart.ChartPoint;
import com.asiainfo.model.chartData.AAAFigure;
import com.asiainfo.model.chartData.BehaviourFigure;
import com.asiainfo.model.chartData.ComplaintFigure;
import com.asiainfo.model.chartData.FaultFigure;
import com.asiainfo.model.chartData.LineFigure;
import com.asiainfo.model.chartData.OptHistory;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.model.chartData.TterminalFigure;
import com.asiainfo.model.chartData.UserFigureInfo;
import com.asiainfo.service.BasicService;
import com.asiainfo.service.UserFigure;
import com.asiainfo.util.ToolsUtils;
import com.asiainfo.util.common.DateUtil;
import com.asiainfo.util.common.Judgment;

@Service
public class UserFigureImpl extends BasicService implements UserFigure {
    private static final String FORMAT = "yyyyMM";
    @Autowired
    private UserBillMapper offnetMapper;
    @Autowired
    private PsProvHistoryMapper historyMapper;
    @Autowired
    private UserBillMapper userBillMapper;

    @Autowired
    private TerminalMapper terminalMapper;

    // @Autowired
    // private CesCrmInfoMapper crmMapper;
    @Autowired
    private CesBpppPcCharMapper pcMapper;
    @Autowired
    private CesBpppComplaintCharMapper complaintMapper;
    @Autowired
    private CesBpppLinenoCharMapper linenoMapper;
    @Autowired
    private CesBpppBehaviorCharMapper behaviorMapper;

    @Override
    public List<ChartDatas> getUserInfo(String account) {
        String premonth = DateUtil.getMinusMonths(FORMAT, 1);
        String month = DateUtil.getMinusMonths(FORMAT, 0);
        List<ChartDatas> ret = new ArrayList<>();
        ret.add(getAAAInfo(account, premonth));
        ret.add(getLineInfo(account, month));
        ret.add(getTterminalInfo(account, month));
        ret.add(getBehaviourInfo(account, month));
        ret.add(getComplaintInfo(account, month));

        return ret;
    }

    /**
     * AAA信息
     * 
     * @param account
     * @param premonth
     * @return
     */
    private ChartDatas getAAAInfo(String account, String premonth) {
        // TODO 需要调整
        AAAFigure t = userBillMapper.getUserInfo(account, premonth.substring(4, 6));
        // AAAFigure t = null;
        if (t == null) {
            t = new AAAFigure();
        }
        ChartDatas datas = new ChartDatas();
        datas.setTitle("AAA信息");
        List<ChartPoint> data = new ArrayList<>();
        data.add(new ChartPoint("月拨号次数", t.getDial_sum() == null ? "--" : t.getDial_sum()));
        data.add(new ChartPoint("月上网时长",
                t.getTimelen_sum() == null ? "--" : t.getTimelen_sum() + "秒"));
        data.add(new ChartPoint("月上行流量",
                t.getInoctets_sum() == null ? "--" : t.getInoctets_sum() + "MB"));
        data.add(new ChartPoint("月下行流量",
                t.getOutoctets_sum() == null ? "--" : t.getOutoctets_sum() + "MB"));
        data.add(new ChartPoint("月上行包数",
                t.getInpackets_sum() == null ? "--" : t.getInpackets_sum()));
        data.add(new ChartPoint("月下行包数",
                t.getOutpackets_sum() == null ? "--" : t.getOutpackets_sum()));
        // data.add(new ChartPoint("最后使用时间",
        // t.getLast_use_time() == null ? "--" : t.getLast_use_time()));
        datas.setData(data);
        return datas;
    }

    /**
     * 终端特征
     * 
     * @param account
     * @param premonth
     * @return
     */
    private ChartDatas getTterminalInfo(String account, String premonth) {
        // TODO 需要调整
        TterminalFigure t = terminalMapper.getTterminalInfo(account, premonth.substring(4, 6));
        // TterminalFigure t = null;
        if (t == null) {
            t = new TterminalFigure();
        }
        ChartDatas datas = new ChartDatas();
        datas.setTitle("终端特征信息");
        List<ChartPoint> data = new ArrayList<>();
        data.add(new ChartPoint("宽带账号", t.getUser_name() == null ? "--" : t.getUser_name()));
        data.add(new ChartPoint("地市", t.getCity_name() == null ? "--" : t.getCity_name()));
        data.add(new ChartPoint("是否质差终端", t.getBadquality() == null ? "--" : t.getBadquality()));
        data.add(new ChartPoint("是否智能家庭网关",
                t.getInteligentgateway() == null ? "--" : t.getInteligentgateway()));
        data.add(
                new ChartPoint("智能网关下挂设备个数", t.getSubdevices() == null ? "--" : t.getSubdevices()));
        datas.setData(data);
        return datas;

    }

    /**
     * CRM信息
     * 
     * @param account
     * @return
     */
    // private ChartDatas getCrmInfo(String account) {
    // String month = DateUtil.getDate(FORMAT);
    // CrmFigure t = crmMapper.getUserInfo(account, month.substring(4, 6));
    // if (t == null) {
    // t = new CrmFigure();
    // }
    // ChartDatas datas = new ChartDatas();
    // datas.setTitle("CRM信息");
    // List<ChartPoint> data = new ArrayList<>();
    // data.add(new ChartPoint("费用类型", ChargeType.getName(t.getCharge_type())));
    // data.add(new ChartPoint("费用金额 ", t.getFee_value() == null ? "--" :
    // t.getFee_value()));
    // data.add(new ChartPoint("受理包期费用", t.getBb_fee() == null ? "--" :
    // t.getBb_fee()));
    // data.add(new ChartPoint("套餐开始时间",
    // t.getProduct_begin_time() == null ? "--" : t.getProduct_begin_time()));
    // data.add(new ChartPoint("套餐结束时间",
    // t.getProduct_end_time() == null ? "--" : t.getProduct_end_time()));
    // data.add(new ChartPoint("订购类型", t.getOrder_type() == null ? "--" :
    // t.getOrder_type()));
    // data.add(new ChartPoint("产品订购时间", t.getOrder_time() == null ? "--" :
    // t.getOrder_time()));
    // data.add(new ChartPoint("手机号码", t.getPhone() == null ? "--" :
    // t.getPhone()));
    // data.add(new ChartPoint("下行宽带速率",
    // t.getBandwidth_down() == null ? "--" : t.getBandwidth_down()));
    // data.add(
    // new ChartPoint("上行宽带速率", t.getBandwidth_up() == null ? "--" :
    // t.getBandwidth_up()));
    // data.add(new ChartPoint("IPTV标识", t.getIptv_flag() == null ? "--" :
    // t.getIptv_flag()));
    // data.add(new ChartPoint("客户名称", t.getCustomer_name() == null ? "--" :
    // t.getIptv_flag()));
    // data.add(new ChartPoint("装机地址", t.getAddress() == null ? "--" :
    // t.getAddress()));
    // datas.setData(data);
    // return datas;
    // }

    /**
     * 报障信息
     * 
     * @param account
     * @param premonth
     * @return
     */
    private ChartDatas getFaultInfo(String account, String premonth) {
        // TODO 需要调整
        // FaultFigure t = pcMapper.getUserInfo(account, premonth.substring(4,
        // 6));
        FaultFigure t = null;
        if (t == null) {
            t = new FaultFigure();
        }
        ChartDatas datas = new ChartDatas();
        datas.setTitle("报障信息");
        List<ChartPoint> data = new ArrayList<>();
        data.add(new ChartPoint("总报障次数", t.getDisabled_num() == null ? "--" : t.getDisabled_num()));
        data.add(new ChartPoint("平均回单时长",
                t.getCall_back_ave() == null ? "--" : t.getCall_back_ave()));
        data.add(new ChartPoint("紧急工单条数", t.getIs_send_urg() == null ? "--" : t.getIs_send_urg()));
        data.add(new ChartPoint("投诉工单次数", t.getSource_com() == null ? "--" : t.getSource_com()));
        data.add(new ChartPoint("群障次数",
                t.getGroup_error_flag_num() == null ? "--" : t.getGroup_error_flag_num()));
        data.add(new ChartPoint("超时限工单次数",
                t.getList_time_out() == null ? "--" : t.getList_time_out()));
        data.add(new ChartPoint("异网报障次数占比", t.getDiff_ratio() == null ? "--" : t.getDiff_ratio()));
        data.add(
                new ChartPoint("固话报障次数占比", t.getFixed_ratio() == null ? "--" : t.getFixed_ratio()));
        data.add(new ChartPoint("平均故障工单处理时长",
                t.getDeal_num_ave() == null ? "--" : t.getDeal_num_ave()));
        data.add(new ChartPoint("平均故障工单自然时长",
                t.getTotal_num_ave() == null ? "--" : t.getTotal_num_ave()));
        data.add(new ChartPoint("总故障工单派单次数",
                t.getService_type_disabled() == null ? "--" : t.getService_type_disabled()));
        data.add(new ChartPoint("故障工单重复处理总次数",
                t.getRep_roce_times() == null ? "--" : t.getRep_roce_times()));
        data.add(new ChartPoint("故障工单客户催单总次数",
                t.getCust_hurry_times() == null ? "--" : t.getCust_hurry_times()));
        data.add(new ChartPoint("故障工单内部催单总次数",
                t.getInner_hurry_times() == null ? "--" : t.getInner_hurry_times()));
        data.add(new ChartPoint("故障工单汇报总次数",
                t.getReport_times() == null ? "--" : t.getReport_times()));
        datas.setData(data);
        return datas;
    }

    /**
     * 投诉/告障信息
     * 
     * @param account
     * @param premonth
     * @return
     */
    private ChartDatas getComplaintInfo(String account, String premonth) {
        // TODO 需要调整
        ComplaintFigure t = complaintMapper.getUserInfo(account, premonth.substring(4, 6));
        // ComplaintFigure t = null;
        if (t == null) {
            t = new ComplaintFigure();
        }
        ChartDatas datas = new ChartDatas();
        datas.setTitle("告障信息");
        List<ChartPoint> data = new ArrayList<>();
        data.add(new ChartPoint("宽带账号", t.getUser_name() == null ? "--" : t.getUser_name()));
        data.add(new ChartPoint("地市", t.getCity_name() == null ? "--" : t.getCity_name()));
        data.add(new ChartPoint("投诉总次数",
                t.getComplaint_total() == null ? "--" : t.getComplaint_total()));
        data.add(new ChartPoint("互联网电视投诉次数	",
                t.getIptv_total() == null ? "--" : t.getIptv_total()));
        data.add(new ChartPoint("家宽投诉次数",
                t.getBroadband_total() == null ? "--" : t.getBroadband_total()));
        data.add(new ChartPoint("产品质量次数",
                t.getFa_productquality() == null ? "--" : t.getFa_productquality()));
        data.add(new ChartPoint("基础服务次数",
                t.getFa_baseservice() == null ? "--" : t.getFa_baseservice()));
        data.add(new ChartPoint("网络质量次数	",
                t.getFa_networkquality() == null ? "--" : t.getFa_networkquality()));
        data.add(new ChartPoint("业务营销次数",
                t.getFa_businessmarketing() == null ? "--" : t.getFa_businessmarketing()));
        data.add(new ChartPoint("内容原因", t.getOr_content() == null ? "--" : t.getOr_content()));
        data.add(new ChartPoint("牌照方问题或平台故障",
                t.getOr_tvplatform() == null ? "--" : t.getOr_tvplatform()));
        data.add(new ChartPoint("前台或市场原因",
                t.getOr_marketing() == null ? "--" : t.getOr_marketing()));
        data.add(new ChartPoint("人为破坏或店里问题", t.getOr_manmade() == null ? "--" : t.getOr_manmade()));
        data.add(new ChartPoint("网络原因", t.getOr_network() == null ? "--" : t.getOr_network()));
        data.add(new ChartPoint("业务管理支撑系统", t.getOr_boss() == null ? "--" : t.getOr_boss()));
        data.add(new ChartPoint("用户原因", t.getOr_user() == null ? "--" : t.getOr_user()));
        data.add(new ChartPoint("装维原因",
                t.getOr_equipmentmaintenance() == null ? "--" : t.getOr_equipmentmaintenance()));
        data.add(new ChartPoint("其他原因", t.getOr_other() == null ? "--" : t.getOr_other()));

        datas.setData(data);
        return datas;
    }

    /**
     * 线路信息
     * 
     * @param account
     * @param premonth
     * @return
     */
    private ChartDatas getLineInfo(String account, String premonth) {
        // TODO 需要调整
        LineFigure t = linenoMapper.getUserInfo(account, premonth.substring(4, 6));
        // LineFigure t =null;
        if (t == null) {
            t = new LineFigure();
        }
        ChartDatas datas = new ChartDatas();
        datas.setTitle("线路信息");
        List<ChartPoint> data = new ArrayList<>();
        data.add(new ChartPoint("宽带账号", t.getUser_name() == null ? "--" : t.getUser_name()));
        data.add(new ChartPoint("地市", t.getCity_name() == null ? "--" : t.getCity_name()));
        data.add(new ChartPoint("BRAS地址", t.getBrasip() == null ? "--" : t.getBrasip()));
        data.add(new ChartPoint("BRAS槽号", t.getSlot() == null ? "--" : t.getSlot()));
        data.add(new ChartPoint("BRAS子槽号", t.getSubslot() == null ? "--" : t.getSubslot()));
        data.add(new ChartPoint("BRAS端口号", t.getPort() == null ? "--" : t.getPort()));
        data.add(new ChartPoint("用户VPI", t.getVip() == null ? "--" : t.getVip()));
        data.add(new ChartPoint("用户VCI", t.getVci() == null ? "--" : t.getVci()));
        data.add(new ChartPoint("用户VLAN", t.getVlan() == null ? "--" : t.getVlan()));
        data.add(new ChartPoint("用户SVLAN", t.getSvlan() == null ? "--" : t.getSvlan()));
        data.add(new ChartPoint("OLT标识", t.getAnid() == null ? "--" : t.getAnid()));
        data.add(new ChartPoint("OLT机架号", t.getAnirack() == null ? "--" : t.getAnirack()));
        data.add(new ChartPoint("OLT机框号", t.getAniframe() == null ? "--" : t.getAniframe()));
        data.add(new ChartPoint("OLT接入槽号", t.getAnislot() == null ? "--" : t.getAnislot()));
        data.add(new ChartPoint("OLT接入子槽号", t.getAnisubslot() == null ? "--" : t.getAnisubslot()));
        data.add(new ChartPoint("ONU标识", t.getOnuid() == null ? "--" : t.getOnuid()));
        data.add(new ChartPoint("PON标识", t.getPon() == null ? "--" : t.getPon()));
        datas.setData(data);
        return datas;
    }

    /**
     * 行为信息
     * 
     * @param account
     * @param premonth
     * @return
     */
    private ChartDatas getBehaviourInfo(String account, String premonth) {
        // TODO 需要调整
        BehaviourFigure t = behaviorMapper.getUserInfo(account, premonth.substring(4, 6));
        // BehaviourFigure t = null;
        if (t == null) {
            t = new BehaviourFigure();
        }
        ChartDatas datas = new ChartDatas();
        datas.setTitle("行为信息");
        List<ChartPoint> data = new ArrayList<>();
        data.add(new ChartPoint("宽带账号", t.getUser_name() == null ? "--" : t.getUser_name()));
        data.add(new ChartPoint("地市", t.getCity_name() == null ? "--" : t.getCity_name()));
        data.add(new ChartPoint("宽带类型", t.getOttonly() == null ? "--" : t.getOttonly()));

        datas.setData(data);
        return datas;
    }

    @Override
    public List<ChartDatas> getUserTendency(String account) {
        List<ParamRelation> attrsh = getNear12Month();
        List<ParamRelation> attrs = getPre1year();
        List<UserFigureInfo> healthdata = offnetMapper.getUserTendencyHealth(account, attrsh);
        List<UserFigureInfo> flowdata = offnetMapper.getUserTendency(account, attrs);
        List<String> titles = Arrays.asList("健康度趋势", "上行流量", "下行流量");
        return useSameMarkData(titles, flowdata, healthdata, attrs, attrsh);
    }

    /**
     * 所有组别的数据都在retdata中的不同列，所有组别数据使用相同的数据点标记attrs
     * 
     * @param titles每组数据的名称
     *            ，相当于echarts中的legend
     * @param retdata数据
     * @param attrs每一个数据点的标记
     *            ，相当于echarts中的x轴名称
     * @return
     */
    protected List<ChartDatas> useSameMarkData(List<String> titles, List<?> retdata,
            List<?> retdatah, List<ParamRelation> attrs, List<ParamRelation> attrsh) {
        if (Judgment.listIsNull(retdata)) {
            return makeEmptyDate(titles, attrs, attrsh);
        }
        List<ChartDatas> ret = new ArrayList<>();
        titles.forEach(title -> {
            if (title.equals("健康度趋势")) {
                ret.add(actualChartDatas(title, retdatah, attrsh));
            } else {
                ret.add(actualChartDatas(title, retdata, attrs));
            }
        });
        return ret;
    }

    protected List<ChartDatas> makeEmptyDate(List<String> titles, List<ParamRelation> attrs,
            List<ParamRelation> attrsh) {
        List<ChartDatas> ret = new ArrayList<>();
        titles.forEach(title -> {
            ChartDatas datas = new ChartDatas();
            if (title.equals("健康度趋势")) {
                datas = emptyChartDatas(title, attrsh);

            } else {
                datas = emptyChartDatas(title, attrs);
            }
            ret.add(datas);
        });
        return ret;
    }

    @Override
    protected String getValue(String title, List<?> retdata, String attr) {
        @SuppressWarnings("unchecked")
        List<UserFigureInfo> datas = (List<UserFigureInfo>) retdata;
        for (UserFigureInfo figures : datas) {
            if (attr.equals(figures.getAttr())) {
                if (title.equals("健康度趋势")) {
                    return figures.getOff_prob() == null ? "0" : figures.getOff_prob();
                } else if (title.equals("上行流量")) {
                    return figures.getInoctets_sum() == null ? "0" : figures.getInoctets_sum();
                } else if (title.equals("下行流量")) {
                    return figures.getOutoctets_sum() == null ? "0" : figures.getOutoctets_sum();
                }
            }
        }
        return "0";
    }

    @Override
    public List<OptHistory> getHistory(String account) {
        return historyMapper.getByName(account);
    }

    public ChartDatas getCharacterHealthVal(String account) {
        ChartDatas chartDatas = new ChartDatas();
        String month = DateUtil.getMonth(0);
        // 查询用户流量健康值（3A特征表）
        String userCharcterTable = "CES_USER_CHARACTER_" + month;
        String userCharcterVal = behaviorMapper.getHealthValByAccount(userCharcterTable, account);
        addAccountHealthList(chartDatas, "用户流量", userCharcterVal);
        // 查询用户上网线路健康值（线路特征表）
        String lineCharcterTable = "CES_LINE_CHARACTER_" + month;
        String lineCharcterVal = behaviorMapper.getHealthValByAccount(lineCharcterTable, account);
        addAccountHealthList(chartDatas, "上网线路", lineCharcterVal);
        // 查询用户家庭终端健康值（终端特征表）
        String terminalCharcterTable = "CES_TERMINAL_CHARACTER_" + month;
        String terminalCharcterVal = behaviorMapper.getHealthValByAccount(terminalCharcterTable,
                account);
        addAccountHealthList(chartDatas, "家庭终端", terminalCharcterVal);

        // 查询用户行为特征健康值（行为特征表）
        String behaviourCharcterTable = "CES_BEHAVIOUR_CHARACTER_" + month;
        String behaviourCharcterVal = behaviorMapper.getHealthValByAccount(behaviourCharcterTable,
                account);
        addAccountHealthList(chartDatas, "用户行为", behaviourCharcterVal);

        // 查询用户投诉告障健康值（投诉特征表）
        String complanintCharcterTable = "CES_COMPLAINT_CHARACTER_" + month;
        String complanintCharcterVal = behaviorMapper.getHealthValByAccount(complanintCharcterTable,
                account);
        if (ToolsUtils.StringIsNull(complanintCharcterVal))
            complanintCharcterVal = "100";
        addAccountHealthList(chartDatas, "投诉告障", complanintCharcterVal);
        return chartDatas;
    }

    private void addAccountHealthList(ChartDatas chartDatas, String name, String val) {
        if (ToolsUtils.StringIsNull(val)) {
            val = "0";
        }
        ChartPoint point = new ChartPoint(name, val);
        chartDatas.getData().add(point);
    }
}
