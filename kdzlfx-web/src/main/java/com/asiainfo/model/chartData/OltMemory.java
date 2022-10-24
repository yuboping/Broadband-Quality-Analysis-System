package com.asiainfo.model.chartData;

import java.util.List;
import java.util.Map;

public class OltMemory {

    private List<Map<String, List<Olt>>> oltMapList;

    private List<Olt> oltList;

    public List<Map<String, List<Olt>>> getOltMapList() {
        return oltMapList;
    }

    public void setOltMapList(List<Map<String, List<Olt>>> oltMapList) {
        this.oltMapList = oltMapList;
    }

    public List<Olt> getOltList() {
        return oltList;
    }

    public void setOltList(List<Olt> oltList) {
        this.oltList = oltList;
    }
}
