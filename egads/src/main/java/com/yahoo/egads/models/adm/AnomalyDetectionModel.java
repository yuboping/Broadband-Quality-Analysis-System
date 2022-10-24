/*
 * Copyright 2015, Yahoo Inc.
 * Copyrights licensed under the GPL License.
 * See the accompanying LICENSE file for terms.
 */

// interface

package com.yahoo.egads.models.adm;

import java.util.Map;

import com.yahoo.egads.data.Anomaly;
import com.yahoo.egads.data.Model;
import com.yahoo.egads.data.TimeSeries;

public interface AnomalyDetectionModel extends Model {
    // methods ////////////////////////////////////////////////

    // returns the type of anomalies detected by the model
    public String getType();

    // tune the anomaly detection parameters based on the training data.
    public void tune(TimeSeries.DataSequence observedSeries, TimeSeries.DataSequence expectedSeries,
            Anomaly.IntervalSequence anomalySequence) throws Exception;

    public Map<String, Object> tuneThreshold(TimeSeries.DataSequence observedSeries,
            TimeSeries.DataSequence expectedSeries, Anomaly.IntervalSequence anomalySequence)
            throws Exception;

    // method to check whether the anomaly value is inside the
    // detection window or not
    public boolean isDetectionWindowPoint(int maxHrsAgo, long windowStart, long anomalyTime,
            long startTime);

    // detect anomalies.
    public Anomaly.IntervalSequence detect(TimeSeries.DataSequence observedSeries,
            TimeSeries.DataSequence expectedSeries) throws Exception;
}
