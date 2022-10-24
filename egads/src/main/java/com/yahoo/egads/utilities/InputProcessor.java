/*
 * Copyright 2015, Yahoo Inc.
 * Copyrights licensed under the GPL License.
 * See the accompanying LICENSE file for terms.
 */

package com.yahoo.egads.utilities;

import java.util.ArrayList;
import java.util.Map;

// An interface which other classes extend
// based on what type of input is passed in.

import java.util.Properties;

import com.yahoo.egads.data.TimeSeries;

public interface InputProcessor {

    void processInput(Properties p) throws Exception;

    Map<String, Object> forecastProcessInput(Properties p,
            ArrayList<TimeSeries> metrics) throws Exception;

    Map<String, Object> forecastProcessInputGUI(Properties p, ArrayList<TimeSeries> metrics)
            throws Exception;
}
