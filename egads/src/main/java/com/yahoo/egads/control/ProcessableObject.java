/*
 * Copyright 2015, Yahoo Inc.
 * Copyrights licensed under the GPL License.
 * See the accompanying LICENSE file for terms.
 */

// An interface for processable object.

package com.yahoo.egads.control;

import java.util.Map;

public interface ProcessableObject {

    // The process() method instantiates the appropriate objects
    // depending on the concrete class implementation.
    public void process() throws Exception;

    // The result() method returns list of anomalies
    public Object result() throws Exception;

    public Map<String, Object> forecastProcess() throws Exception;

    public Map<String, Object> forecastProcessGUI() throws Exception;

}
