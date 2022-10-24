package com.yahoo.egads;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.yahoo.egads.data.Anomaly;
import com.yahoo.egads.data.Anomaly.Interval;
import com.yahoo.egads.data.Anomaly.IntervalSequence;
import com.yahoo.egads.data.TimeSeries;
import com.yahoo.egads.data.TimeSeries.DataSequence;
import com.yahoo.egads.data.TimeSeries.Entry;
import com.yahoo.egads.models.kdzlfx.Olt;
import com.yahoo.egads.utilities.FileUtils;
import com.yahoo.egads.utilities.InputProcessor;
import com.yahoo.egads.utilities.StdinProcessor;

public class kdzlfx {

    public static String SAMPLE_CONFIG = "src/test/resources/sample_config.ini";

    public static String MAX_ANOMS = "0.2";

    public static String ALARM = "1";

    public static String NOALARM = "0";

    /**
     * 
     * @Title: forecastOlt @Description: TODO(预测OLT) @param @param
     *         oltList @param @return @param @throws Exception 参数 @return
     *         List<Olt> 返回类型 @throws
     */
    public static List<Olt> forecastOlt(List<Olt> oltList, String config, String max_anoms)
            throws Exception {
        List<Olt> resultOltList = new ArrayList<Olt>();
        if (oltList != null && !oltList.isEmpty()) {
            // 数据过滤
            List<Olt> oltFilterList = dataFilter(oltList);
            Properties p = new Properties();
            File f = new File(config);
            boolean isRegularFile = f.exists();
            if (isRegularFile) {
                InputStream is = new FileInputStream(config);
                p.load(is);
            } else {
                FileUtils.initProperties(config, p);
            }
            InputProcessor ip = new StdinProcessor();
            ArrayList<TimeSeries> metrics = createTimeSeries(oltFilterList, p);
            Map<String, Object> map = ip.forecastProcessInput(p, metrics);
            @SuppressWarnings("unchecked")
            ArrayList<TimeSeries.DataSequence> dataSequencelist = (ArrayList<DataSequence>) map
                    .get("dataSequence");
            @SuppressWarnings("unchecked")
            ArrayList<Anomaly> anomalyList = (ArrayList<Anomaly>) map.get("anomalyList");
            IntervalSequence intervals = anomalyList.get(0).intervals;
            List<String> oltTimeList = new ArrayList<String>();
            for (int i = 0; i < dataSequencelist.get(0).size(); i++) {
                Entry dataEntry = dataSequencelist.get(0).get(i);
                String oltName = timeStampChangeString(dataEntry.time, "yyyy-MM-dd HH:mm:ss");
                Olt olt = oltFilterList.get(0);
                for (Olt oltO : oltFilterList) {
                    if (oltO.getName().equals(oltName)) {
                        olt = oltO;
                        break;
                    }
                }
                if (!oltTimeList.contains(oltName)) {
                    oltTimeList.add(oltName);
                    DecimalFormat df = new DecimalFormat("##.##");
                    String forecastValue = df.format(dataEntry.value);
                    Olt resultOlt = new Olt();
                    resultOlt.setOltip(olt.getOltip());
                    resultOlt.setName(oltName);
                    resultOlt.setValue(olt.getValue());
                    resultOlt.setForecastValue(forecastValue);
                    setUpperLowerValue(olt, resultOlt, max_anoms);
                    resultOlt.setIsalarm(NOALARM);
                    for (Interval interval : intervals) {
                        String startTime = timeStampChangeString(interval.startTime,
                                "yyyy-MM-dd HH:mm:ss");
                        if (startTime.equals(oltName)) {
                            resultOlt.setIsalarm(ALARM);
                            break;
                        }
                    }
                    resultOltList.add(resultOlt);
                }
            }
        }
        return resultOltList;
    }

    public static List<Olt> dataFilter(List<Olt> oltList) {
        List<Olt> oltFilterList = new ArrayList<Olt>();
        boolean isfirst = true;
        boolean issecond = true;
        long beforeTime = 0;
        long beforeDValue = 0;
        Olt beforeOlt = new Olt();
        try {
            for (Olt olt : oltList) {
                long time = stringChangeTimeStamp(olt.getName(), "yyyy-MM-dd HH:mm:ss");
                if (isfirst) {
                    isfirst = false;
                    beforeTime = time;
                    beforeOlt = olt;
                    oltFilterList.add(olt);
                    continue;
                }
                if (issecond) {
                    issecond = false;
                    beforeDValue = time - beforeTime;
                    beforeTime = time;
                    beforeOlt = olt;
                    oltFilterList.add(olt);
                    continue;
                }
                long currentDValue = time - beforeTime;
                if (beforeDValue == currentDValue) {
                    oltFilterList.add(olt);
                } else {
                    oltFilterList.clear();
                    oltFilterList.add(beforeOlt);
                    oltFilterList.add(olt);
                }
                beforeTime = time;
                beforeOlt = olt;
                beforeDValue = currentDValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oltFilterList;
    }

    /**
     * 
     * @Title: setUpperLowerValue @Description: TODO(余数的异常检测) @param @param
     *         olt @param @param forecastOlt 参数 @return void 返回类型 @throws
     */
    public static void setUpperLowerValue(Olt olt, Olt forecastOlt, String max_anoms) {
        double forecastValue = Double.valueOf(forecastOlt.getForecastValue());
        double upperValue = forecastValue * (1 + Double.valueOf(max_anoms));
        double lowerValue = forecastValue * (1 - Double.valueOf(max_anoms));
        java.text.NumberFormat NF = java.text.NumberFormat.getInstance();
        NF.setMaximumFractionDigits(15);
        NF.setMinimumFractionDigits(0);
        NF.setGroupingUsed(false);
        NF.setMaximumFractionDigits(0);
        NF.setMinimumFractionDigits(0);
        forecastOlt.setForecastValue(NF.format(forecastValue));
        forecastOlt.setUpperValue(NF.format(upperValue));
        forecastOlt.setLowerValue(NF.format(lowerValue));
        // double value = Double.valueOf(olt.getValue());
        // if ((value >= Double.valueOf(forecastOlt.getLowerValue()))
        // && (value <= Double.valueOf(forecastOlt.getUpperValue()))) {
        // forecastOlt.setIsalarm(NOALARM);
        // } else {
        // forecastOlt.setIsalarm(ALARM);
        // }
    }

    public static ArrayList<TimeSeries> createTimeSeries(List<Olt> oltList, Properties config) {
        ArrayList<TimeSeries> output = new ArrayList<TimeSeries>();
        Integer aggr = 1;
        if (config.getProperty("AGGREGATION") != null) {
            aggr = new Integer(config.getProperty("AGGREGATION"));
        }
        boolean isfirst = true;
        try {
            for (Olt olt : oltList) {
                if (isfirst) {
                    TimeSeries ts = new TimeSeries();
                    ts.meta.fileName = olt.getOltip();
                    output.add(ts);
                    isfirst = false;
                }
                long time = stringChangeTimeStamp(olt.getName(), "yyyy-MM-dd HH:mm:ss");
                float value = Float.parseFloat(olt.getValue());
                output.get(0).append(time, value);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Handle aggregation.
        if (aggr > 1) {
            for (TimeSeries t : output) {
                t.data = t.aggregate(aggr);
                t.meta.name += "_aggr_" + aggr;
            }
        }
        return output;
    }

    /**
     * 
     * @Title: stringChangeTimeStamp @Description:
     *         TODO(日期格式字符串转换成时间戳) @param @param timeString @param @param
     *         format @param @return @param @throws Exception 参数 @return Long
     *         返回类型 @throws
     */
    public static Long stringChangeTimeStamp(String timeString, String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Long date = sdf.parse(timeString).getTime() / 1000L;
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
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    public static void main(String[] args) {
        List<Olt> olt = new ArrayList<Olt>();
        Olt l0 = new Olt();
        l0.setOltip("10.21.17.179");
        l0.setName("2014-09-30 08:00:00");
        l0.setValue("10");
        olt.add(l0);
        Olt l1 = new Olt();
        l1.setOltip("10.21.17.179");
        l1.setName("2014-09-30 09:00:00");
        l1.setValue("100");
        olt.add(l1);
        Olt l2 = new Olt();
        l2.setOltip("10.21.17.179");
        l2.setName("2014-09-30 10:00:00");
        l2.setValue("10");
        olt.add(l2);
        Olt l3 = new Olt();
        l3.setOltip("10.21.17.122");
        l3.setName("2014-09-30 12:00:00");
        l3.setValue("100");
        olt.add(l3);
        Olt l4 = new Olt();
        l4.setOltip("10.21.17.179");
        l4.setName("2014-09-30 13:00:00");
        l4.setValue("10000");
        olt.add(l4);
        List<Olt> forecastOltList = new ArrayList<Olt>();
        try {
            forecastOltList = forecastOlt(olt, SAMPLE_CONFIG, MAX_ANOMS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }
}
