package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.entity.Timeseries;

import java.util.List;

public interface TimeseriesService {

    List<Timeseries> findAll();

    List<Timeseries> save(List<Timeseries> timeSeriesList);
}
