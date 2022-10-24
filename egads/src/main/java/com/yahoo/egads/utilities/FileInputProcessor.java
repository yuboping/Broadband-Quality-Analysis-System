/*
 * Copyright 2015, Yahoo Inc.
 * Copyrights licensed under the GPL License.
 * See the accompanying LICENSE file for terms.
 */

package com.yahoo.egads.utilities;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

// Class that implements EGADS file input processing.

import com.yahoo.egads.control.ProcessableObject;
import com.yahoo.egads.control.ProcessableObjectFactory;
import com.yahoo.egads.data.TimeSeries;

public class FileInputProcessor implements InputProcessor {

    private String file = null;

    public FileInputProcessor(String file) {
        this.file = file;
    }

    public void processInput(Properties p) throws Exception {
        // Parse the input timeseries.
        ArrayList<TimeSeries> metrics = com.yahoo.egads.utilities.FileUtils
                .createTimeSeries(this.file, p);
        for (TimeSeries ts : metrics) {
            ProcessableObject po = ProcessableObjectFactory.create(ts, p);
            po.process();
        }
    }

    @Override
    public Map<String, Object> forecastProcessInput(Properties p, ArrayList<TimeSeries> metrics)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> forecastProcessInputGUI(Properties p, ArrayList<TimeSeries> metrics)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
