package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.entity.Timeseries;
import com.yavor.projects.weather.api.repository.TimeseriesRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TimeseriesServiceImpl implements TimeseriesService {

    private final TimeseriesRepository timeseriesRepository;

    public TimeseriesServiceImpl(TimeseriesRepository timeseriesRepository) {
        this.timeseriesRepository = timeseriesRepository;
    }

    @Override
    public List<Timeseries> findTimeseriesByTypeForPeriod(String type, Date from, Date to) {
        return timeseriesRepository.findTimeseriesByTypeForPeriod(type, from, to);
    }

    @Override
    public List<Timeseries> save(List<Timeseries> timeSeriesList) {
        if (timeSeriesList == null || timeSeriesList.isEmpty()) {
            throw new IllegalArgumentException("Timeseries are empty !");
        }
        var date = new Date();
        for (var t : timeSeriesList) {
            t.setInsertionTime(date);
            timeseriesRepository.save(t);
        }
        return timeSeriesList;
    }

}
