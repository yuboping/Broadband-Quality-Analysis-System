package com.yahoo.egads.models.asiainfo;

public class TimeSeriesData {

    private long timestamp;

    private float value;

    private float forecastValue;

    private float mapee;

    private float mae;

    private float smape;

    private float mape;

    private float mase;

    private Boolean isalarm;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getForecastValue() {
        return forecastValue;
    }

    public void setForecastValue(float forecastValue) {
        this.forecastValue = forecastValue;
    }

    public float getMapee() {
        return mapee;
    }

    public void setMapee(float mapee) {
        this.mapee = mapee;
    }

    public float getMae() {
        return mae;
    }

    public void setMae(float mae) {
        this.mae = mae;
    }

    public float getSmape() {
        return smape;
    }

    public void setSmape(float smape) {
        this.smape = smape;
    }

    public float getMape() {
        return mape;
    }

    public void setMape(float mape) {
        this.mape = mape;
    }

    public float getMase() {
        return mase;
    }

    public void setMase(float mase) {
        this.mase = mase;
    }

    public Boolean getIsalarm() {
        return isalarm;
    }

    public void setIsalarm(Boolean isalarm) {
        this.isalarm = isalarm;
    }
}
