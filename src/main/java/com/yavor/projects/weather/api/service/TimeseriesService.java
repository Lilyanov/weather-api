package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.entity.Timeseries;

import java.util.Date;
import java.util.List;

public interface TimeseriesService {

    List<Timeseries> findTimeseriesByTypeForPeriod(String type, Date from, Date to);

    List<Timeseries> save(List<Timeseries> timeSeriesList);
}
