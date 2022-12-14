/*
 * Copyright 2015, Yahoo Inc.
 * Copyrights licensed under the GPL License.
 * See the accompanying LICENSE file for terms.
 */

// A template for updateing the model given the data and the model config.

package com.yahoo.egads.control;

import java.util.Map;
import java.util.Properties;

import com.yahoo.egads.data.TimeSeries;

public class UpdateModelProcessable implements ProcessableObject {

    private ModelAdapter ma;
    private TimeSeries.DataSequence newData;
    private Properties config;

    UpdateModelProcessable(ModelAdapter ma, TimeSeries.DataSequence newData, Properties config) {
        this.ma = ma;
        this.newData = newData;
        this.config = config;
    }

    public void process() throws Exception {
        this.ma.train();
        this.ma.update(this.newData);
    }

    public Object result() throws Exception {
        return "Updated";
    }

    @Override
    public Map<String, Object> forecastProcess() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> forecastProcessGUI() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
