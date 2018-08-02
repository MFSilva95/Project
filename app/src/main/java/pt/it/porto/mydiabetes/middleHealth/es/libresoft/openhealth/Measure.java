package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth;

import java.util.ArrayList;

public class Measure {

    private int MeasureId;
    private String MeasureName;
    private int unitId;
    private String unitName;
    private long timestamp;
    private ArrayList<Double> values;
    private ArrayList<Integer> metricIds;
    private ArrayList<String> metricNames;

    public Measure() {
        MeasureId = -1;
        MeasureName = "unknown";
        unitId = -1;
        unitName = "unknown";
        timestamp = -1;
        values = new ArrayList<>();
        metricIds = new ArrayList<>();
        metricNames = new ArrayList<>();
    }


    public void add(double value) {
        values.add(value);
    }


    public void add(int id) {
        metricIds.add(id);
    }

    public void add(String name) {
        metricNames.add(name);
    }


    public ArrayList<String> getMetricNames() {
        return metricNames;
    }


    public ArrayList<Double> getValues() {
        return values;
    }

    public ArrayList<Integer> getMetricIds() {
        return metricIds;
    }

    public int getMeasureId() {
        return MeasureId;
    }

    public void setMeasureId(int measureId) {
        MeasureId = measureId;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMeasureName() {
        return MeasureName;
    }

    public void setMeasureName(String measureName) {
        MeasureName = measureName;
    }


}
