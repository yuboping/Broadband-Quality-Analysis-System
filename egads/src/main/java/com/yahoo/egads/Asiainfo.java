package com.yahoo.egads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.yahoo.egads.data.Anomaly;
import com.yahoo.egads.data.Anomaly.Interval;
import com.yahoo.egads.data.Anomaly.IntervalSequence;
import com.yahoo.egads.data.TimeSeries;
import com.yahoo.egads.data.TimeSeries.DataSequence;
import com.yahoo.egads.data.TimeSeries.Entry;
import com.yahoo.egads.models.asiainfo.ConstantUtil;
import com.yahoo.egads.models.asiainfo.TimeSeriesData;
import com.yahoo.egads.models.asiainfo.TimeSeriesResult;
import com.yahoo.egads.utilities.FileUtils;
import com.yahoo.egads.utilities.InputProcessor;
import com.yahoo.egads.utilities.StdinProcessor;

public class Asiainfo {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 
     * @Title: egadsTimeSeriesFile @Description:
     *         TODO(文件类型的时间序列数据自动异常检测算法) @param @param input_file @param @param
     *         output_file @param @param config 参数 @return void 返回类型 @throws
     */
    public static void egadsTimeSeriesFile(String input_file, String output_file, String config) {
        BufferedInputStream bufferedInput = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            bufferedInput = new BufferedInputStream(new FileInputStream(input_file));
            bufferedOutput = new BufferedOutputStream(new FileOutputStream(output_file));
            String newLine = System.getProperty("line.separator");
            bufferedOutput.write("TIMESTAMP,VALUE,FORECASTVALUE,ISALARM".getBytes());
            bufferedOutput.write(newLine.getBytes());
            TimeSeriesResult timeSeriesResult = egadsTimeSeriesStream(bufferedInput, config);
            if (timeSeriesResult.getResultDataList() != null
                    && !timeSeriesResult.getResultDataList().isEmpty()) {
                for (TimeSeriesData timeSeriesDataforecast : timeSeriesResult.getResultDataList()) {
                    String timestamp = String.valueOf(timeSeriesDataforecast.getTimestamp());
                    DecimalFormat df = new DecimalFormat("##");
                    String value = df.format(timeSeriesDataforecast.getValue());
                    String forecastValue = df.format(timeSeriesDataforecast.getForecastValue());
                    String isalarm = String.valueOf(timeSeriesDataforecast.getIsalarm());
                    String resultLine = timestamp + "," + value + "," + forecastValue + ","
                            + isalarm;
                    bufferedOutput.write(resultLine.getBytes());
                    bufferedOutput.write(newLine.getBytes());
                }
            }
            bufferedOutput.close();
            bufferedInput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @Title: egadsTimeSeriesStream @Description:
     *         TODO(数据流类型的时间序列数据自动异常检测算法) @param @param
     *         bufferedInputStream @param @param config @param @return
     *         参数 @return TimeSeriesResult 返回类型 @throws
     */
    public static TimeSeriesResult egadsTimeSeriesStream(BufferedInputStream bufferedInputStream,
            String config) {
        TimeSeriesResult timeSeriesResult = new TimeSeriesResult();
        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        String content = null;
        try {
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                content += new String(buffer, 0, bytesRead);
            }
            String[] contentArr = content.split("\r\n");
            boolean isfirst = true;
            List<TimeSeriesData> timeSeriesDataList = new ArrayList<TimeSeriesData>();
            for (String timeValueArr : contentArr) {
                if (isfirst) {
                    isfirst = false;
                    continue;
                } else {
                    String[] timeValue = timeValueArr.split(",");
                    TimeSeriesData timeSeriesData = new TimeSeriesData();
                    timeSeriesData.setTimestamp(Long.parseLong(timeValue[0]));
                    timeSeriesData.setValue(Float.parseFloat(timeValue[1]));
                    if (timeSeriesData != null) {
                        timeSeriesDataList.add(timeSeriesData);
                    }
                }
            }
            timeSeriesResult = egadsTimeSeries(timeSeriesDataList, ConstantUtil.SAMPLE_CONFIG);
        } catch (IOException e) {
            timeSeriesResult.setMesCode(ConstantUtil.MESCODE_401);
            timeSeriesResult.setMesgDesc(e.toString());
        }
        return timeSeriesResult;
    }

    /**
     * 
     * @Title: egadsTimeSeries @Description:
     *         TODO(实体类时间序列数据自动异常检测算法) @param @param requestList @param @param
     *         config @param @return 参数 @return TimeSeriesResult 返回类型 @throws
     */
    @SuppressWarnings("unchecked")
    public static TimeSeriesResult egadsTimeSeries(List<TimeSeriesData> requestList,
            String config) {
        TimeSeriesResult timeSeriesResult = new TimeSeriesResult();
        timeSeriesResult.setMesCode(ConstantUtil.MESCODE_200);
        timeSeriesResult.setMesgDesc("Success");
        if (requestList != null && !requestList.isEmpty()) {
            try {
                // 原始数据检测并过滤
                List<TimeSeriesData> filterDataList = dataFilter(requestList);
                if (filterDataList.size() != requestList.size()) {
                    // 原始数据时间序列不规整
                    timeSeriesResult.setMesCode(ConstantUtil.MESCODE_202);
                    timeSeriesResult.setMesgDesc("Irregular time series of original data");
                }
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
                // 生成TimeSeries
                ArrayList<TimeSeries> metrics = createTimeSeries(filterDataList, p);
                // 时间序列分解预测和异常检测算法调用
                Map<String, Object> map = ip.forecastProcessInput(p, metrics);
                // 时间序列分解预测结果
                ArrayList<TimeSeries.DataSequence> dataSequencelist = (ArrayList<DataSequence>) map
                        .get("dataSequence");
                // 异常检测结果
                ArrayList<Anomaly> anomalyList = (ArrayList<Anomaly>) map.get("anomalyList");
                // 检测的函数结果
                Map<String, Object> threshold = (Map<String, Object>) map.get("threshold");
                timeSeriesResult.setThreshold(threshold);
                HashMap<String, ArrayList<Float>> allErrors = new HashMap<String, ArrayList<Float>>();
                ArrayList<Float> mapee = new ArrayList<Float>();
                ArrayList<Float> mae = new ArrayList<Float>();
                ArrayList<Float> smape = new ArrayList<Float>();
                ArrayList<Float> mape = new ArrayList<Float>();
                ArrayList<Float> mase = new ArrayList<Float>();
                Boolean isexistthreshold = false;
                if (null != threshold) {
                    isexistthreshold = true;
                    allErrors = (HashMap<String, ArrayList<Float>>) threshold.get("allErrors");
                    mapee = allErrors.get("mapee");
                    mae = allErrors.get("mae");
                    smape = allErrors.get("smape");
                    mape = allErrors.get("mape");
                    mase = allErrors.get("mase");
                }
                IntervalSequence intervals = anomalyList.get(0).intervals;
                List<TimeSeriesData> resultDataList = new ArrayList<TimeSeriesData>();
                List<Object> timeList = new ArrayList<Object>();
                for (int i = 0; i < dataSequencelist.get(0).size(); i++) {
                    Entry dataEntry = dataSequencelist.get(0).get(i);
                    TimeSeriesData filterTimeSeriesData = new TimeSeriesData();
                    for (TimeSeriesData filterData : filterDataList) {
                        if (filterData.getTimestamp() == dataEntry.time) {
                            filterTimeSeriesData = filterData;
                            break;
                        }
                    }
                    // 过滤重复数据
                    if (!timeList.contains(dataEntry.time)) {
                        timeList.add(dataEntry.time);
                        TimeSeriesData timeSeriesData = new TimeSeriesData();
                        timeSeriesData.setTimestamp(dataEntry.time);
                        if (filterTimeSeriesData != null) {
                            timeSeriesData.setValue(filterTimeSeriesData.getValue());
                        }
                        timeSeriesData.setForecastValue(dataEntry.value);
                        if (isexistthreshold) {
                            timeSeriesData.setMapee(mapee.get(i));
                            timeSeriesData.setMae(mae.get(i));
                            timeSeriesData.setSmape(smape.get(i));
                            timeSeriesData.setMape(mape.get(i));
                            timeSeriesData.setMase(mase.get(i));
                        }
                        timeSeriesData.setIsalarm(false);
                        for (Interval interval : intervals) {
                            if (interval.startTime == dataEntry.time) {
                                timeSeriesData.setIsalarm(true);
                                break;
                            }
                        }
                        resultDataList.add(timeSeriesData);
                    }
                }
                timeSeriesResult.setResultDataList(resultDataList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                timeSeriesResult.setMesCode(ConstantUtil.MESCODE_400);
                timeSeriesResult.setMesgDesc(e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                timeSeriesResult.setMesCode(ConstantUtil.MESCODE_401);
                timeSeriesResult.setMesgDesc(e.toString());
            } catch (Exception e) {
                e.printStackTrace();
                timeSeriesResult.setMesCode(ConstantUtil.MESCODE_402);
                timeSeriesResult.setMesgDesc(e.toString());
            }

        } else {
            // 原始数据为空
            timeSeriesResult.setMesCode(ConstantUtil.MESCODE_201);
            timeSeriesResult.setMesgDesc("Original data is empty");
        }
        return timeSeriesResult;
    }

    /**
     * 
     * @Title: dataFilter @Description: TODO(原始数据检测并过滤) @param @param
     *         requestList @param @return 参数 @return List<TimeSeriesData>
     *         返回类型 @throws
     */
    public static List<TimeSeriesData> dataFilter(List<TimeSeriesData> requestList) {
        List<TimeSeriesData> dataFilter = new ArrayList<TimeSeriesData>();
        boolean isfirst = true;
        boolean issecond = true;
        long beforeTime = 0;
        long beforeDValue = 0;
        TimeSeriesData beforeTimeSeriesData = new TimeSeriesData();
        for (TimeSeriesData timeSeriesData : requestList) {
            if (timeSeriesData != null) {
                long time = timeSeriesData.getTimestamp();
                if (isfirst) {
                    isfirst = false;
                    beforeTime = time;
                    beforeTimeSeriesData = timeSeriesData;
                    dataFilter.add(timeSeriesData);
                    continue;
                }
                if (issecond) {
                    issecond = false;
                    beforeDValue = time - beforeTime;
                    beforeTime = time;
                    beforeTimeSeriesData = timeSeriesData;
                    dataFilter.add(timeSeriesData);
                    continue;
                }
                long currentDValue = time - beforeTime;
                if (beforeDValue == currentDValue) {
                    dataFilter.add(timeSeriesData);
                } else {
                    dataFilter.clear();
                    dataFilter.add(beforeTimeSeriesData);
                    dataFilter.add(timeSeriesData);
                }
                beforeTime = time;
                beforeTimeSeriesData = timeSeriesData;
                beforeDValue = currentDValue;
            }
        }
        return dataFilter;
    }

    /**
     * 
     * @Title: createTimeSeries @Description: TODO(初始化时间序列) @param @param
     *         filterDataList @param @param config @param @return 参数 @return
     *         ArrayList<TimeSeries> 返回类型 @throws
     */
    public static ArrayList<TimeSeries> createTimeSeries(List<TimeSeriesData> filterDataList,
            Properties config) {
        ArrayList<TimeSeries> output = new ArrayList<TimeSeries>();
        Integer aggr = 1;
        if (config.getProperty("AGGREGATION") != null) {
            aggr = new Integer(config.getProperty("AGGREGATION"));
        }
        boolean isfirst = true;
        try {
            for (TimeSeriesData timeSeriesData : filterDataList) {
                if (isfirst) {
                    TimeSeries ts = new TimeSeries();
                    output.add(ts);
                    isfirst = false;
                }
                output.get(0).append(timeSeriesData.getTimestamp(), timeSeriesData.getValue());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * @Title: egadsTimeSeries @Description:
     *         TODO(实体类时间序列数据自动异常检测算法GUI显示) @param @param
     *         requestList @param @param config @param @return 参数 @return
     *         TimeSeriesResult 返回类型 @throws
     */
    public static TimeSeriesResult egadsTimeSeriesGUI(List<TimeSeriesData> requestList,
            String config) {
        TimeSeriesResult timeSeriesResult = new TimeSeriesResult();
        timeSeriesResult.setMesCode(ConstantUtil.MESCODE_200);
        timeSeriesResult.setMesgDesc("Success");
        if (requestList != null && !requestList.isEmpty()) {
            try {
                // 原始数据检测并过滤
                List<TimeSeriesData> filterDataList = dataFilter(requestList);
                if (filterDataList.size() != requestList.size()) {
                    // 原始数据时间序列不规整
                    timeSeriesResult.setMesCode(ConstantUtil.MESCODE_202);
                    timeSeriesResult.setMesgDesc("Irregular time series of original data");
                }
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
                // 生成TimeSeries
                ArrayList<TimeSeries> metrics = createTimeSeries(filterDataList, p);
                // 时间序列分解预测和异常检测算法调用
                Map<String, Object> map = ip.forecastProcessInputGUI(p, metrics);
                // 时间序列分解预测结果
                @SuppressWarnings("unchecked")
                ArrayList<TimeSeries.DataSequence> dataSequencelist = (ArrayList<DataSequence>) map
                        .get("dataSequence");
                // 异常检测结果
                @SuppressWarnings("unchecked")
                ArrayList<Anomaly> anomalyList = (ArrayList<Anomaly>) map.get("anomalyList");
                IntervalSequence intervals = anomalyList.get(0).intervals;
                List<TimeSeriesData> resultDataList = new ArrayList<TimeSeriesData>();
                List<Object> timeList = new ArrayList<Object>();
                for (int i = 0; i < dataSequencelist.get(0).size(); i++) {
                    Entry dataEntry = dataSequencelist.get(0).get(i);
                    TimeSeriesData filterTimeSeriesData = new TimeSeriesData();
                    for (TimeSeriesData filterData : filterDataList) {
                        if (filterData.getTimestamp() == dataEntry.time) {
                            filterTimeSeriesData = filterData;
                            break;
                        }
                    }
                    // 过滤重复数据
                    if (!timeList.contains(dataEntry.time)) {
                        timeList.add(dataEntry.time);
                        TimeSeriesData timeSeriesData = new TimeSeriesData();
                        timeSeriesData.setTimestamp(dataEntry.time);
                        if (filterTimeSeriesData != null) {
                            timeSeriesData.setValue(filterTimeSeriesData.getValue());
                        }
                        timeSeriesData.setForecastValue(dataEntry.value);
                        timeSeriesData.setIsalarm(false);
                        for (Interval interval : intervals) {
                            if (interval.startTime == dataEntry.time) {
                                timeSeriesData.setIsalarm(true);
                                break;
                            }
                        }
                        resultDataList.add(timeSeriesData);
                    }
                }
                timeSeriesResult.setResultDataList(resultDataList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                timeSeriesResult.setMesCode(ConstantUtil.MESCODE_400);
                timeSeriesResult.setMesgDesc(e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                timeSeriesResult.setMesCode(ConstantUtil.MESCODE_401);
                timeSeriesResult.setMesgDesc(e.toString());
            } catch (Exception e) {
                e.printStackTrace();
                timeSeriesResult.setMesCode(ConstantUtil.MESCODE_402);
                timeSeriesResult.setMesgDesc(e.toString());
            }

        } else {
            // 原始数据为空
            timeSeriesResult.setMesCode(ConstantUtil.MESCODE_201);
            timeSeriesResult.setMesgDesc("Original data is empty");
        }
        return timeSeriesResult;
    }

    // public static void main(String[] args) {
    // System.out.println("start");
    // egadsTimeSeriesFile("src/test/resources/sample_input.csv",
    // "src/test/resources/sample_output.csv", ConstantUtil.SAMPLE_CONFIG);
    // List<TimeSeriesData> requestList = new ArrayList<TimeSeriesData>();
    // TimeSeriesData timeSeriesData1 = new TimeSeriesData();
    // timeSeriesData1.setTimestamp(1568109400);
    // timeSeriesData1.setValue(376);
    // requestList.add(timeSeriesData1);
    // TimeSeriesData timeSeriesData2 = new TimeSeriesData();
    // timeSeriesData2.setTimestamp(1568113200);
    // timeSeriesData2.setValue(374);
    // requestList.add(timeSeriesData2);
    // TimeSeriesData timeSeriesData3 = new TimeSeriesData();
    // timeSeriesData3.setTimestamp(1568116800);
    // timeSeriesData3.setValue(377);
    // requestList.add(timeSeriesData3);
    // TimeSeriesData timeSeriesData4 = new TimeSeriesData();
    // timeSeriesData4.setTimestamp(1568120400);
    // timeSeriesData4.setValue(372);
    // requestList.add(timeSeriesData4);
    // TimeSeriesData timeSeriesData5 = new TimeSeriesData();
    // timeSeriesData5.setTimestamp(1568124000);
    // timeSeriesData5.setValue(373);
    // requestList.add(timeSeriesData5);
    // TimeSeriesResult timeSeriesResult = egadsTimeSeries(requestList,
    // ConstantUtil.SAMPLE_CONFIG);
    // System.out.println("end");
    // }
}
