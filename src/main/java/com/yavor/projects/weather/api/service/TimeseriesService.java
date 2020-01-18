package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.dto.TimeseriesGroup;
import com.yavor.projects.weather.api.entity.Timeseries;

import java.util.Date;
import java.util.List;

public interface TimeseriesService {

    List<TimeseriesGroup> findTimeseriesGroups(List<String> types, Date from, Date to);

    List<Timeseries> findTimeseriesByTypeForPeriod(String type, Date from, Date to);

    List<Timeseries> save(List<Timeseries> timeSeriesList);

    void sendTimeseries(List<Timeseries> timeseriesList);
}
