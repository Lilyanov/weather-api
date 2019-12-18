package com.yavor.projects.weather.api.dto;

import com.yavor.projects.weather.api.entity.Timeseries;

import java.util.List;

public class TimeseriesGroup {

    private String type;

    private List<Timeseries> timeseries;

    public TimeseriesGroup() {

    }

    public TimeseriesGroup(String type, List<Timeseries> timeseries) {
        this.type = type;
        this.timeseries = timeseries;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Timeseries> getTimeseries() {
        return timeseries;
    }

    public void setTimeseries(List<Timeseries> timeseries) {
        this.timeseries = timeseries;
    }
}
