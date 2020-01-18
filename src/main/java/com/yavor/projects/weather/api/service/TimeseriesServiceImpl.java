package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.dto.TimeseriesGroup;
import com.yavor.projects.weather.api.entity.Timeseries;
import com.yavor.projects.weather.api.repository.TimeseriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeseriesServiceImpl implements TimeseriesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesServiceImpl.class);

    private final TimeseriesRepository timeseriesRepository;
    private final RealTimeService realTimeService;

    public TimeseriesServiceImpl(TimeseriesRepository timeseriesRepository, RealTimeService realTimeService) {
        this.realTimeService = realTimeService;
        this.timeseriesRepository = timeseriesRepository;
    }

    @Override
    public List<TimeseriesGroup> findTimeseriesGroups(List<String> types, Date from, Date to) {
        if (types == null || types.isEmpty()) {
            return new ArrayList<>();
        }
        return types.parallelStream()
                .map(type -> {
                    var timeseries = findTimeseriesByTypeForPeriod(type, from, to);
                    return new TimeseriesGroup(type, timeseries);
                })
                .collect(Collectors.toList());
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

    @Override
    public void sendTimeseries(List<Timeseries> timeseriesList) {

        var tsGroups = timeseriesList.stream().map(ts -> {
            var tsGroup = new TimeseriesGroup();
            tsGroup.setTimeseries(List.of(ts));
            tsGroup.setType(ts.getType());
            return tsGroup;
        }).collect(Collectors.toList());

        this.realTimeService.sendTimeseries(tsGroups);
    }
}
