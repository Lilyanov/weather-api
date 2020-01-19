package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.dto.TimeseriesGroup;
import com.yavor.projects.weather.api.entity.Timeseries;
import com.yavor.projects.weather.api.repository.TimeseriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TimeseriesServiceImpl implements TimeseriesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesServiceImpl.class);

    private final TimeseriesRepository timeseriesRepository;
    private final RealTimeService realTimeService;
    private final Calendar cal;

    public TimeseriesServiceImpl(TimeseriesRepository timeseriesRepository, RealTimeService realTimeService) {
        this.realTimeService = realTimeService;
        this.timeseriesRepository = timeseriesRepository;
        this.cal = Calendar.getInstance();
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

        var timeseries = timeseriesRepository.findTimeseriesByTypeForPeriod(type, from, to);
        var diffInMillies = Math.abs(to.getTime() - from.getTime());
        var diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diff <= 2) {
            return timeseries;
        } else if (diff <= 7) {
            return aggregateTimeseries(timeseries, 1);
        } else if (diff <= 30) {
            return aggregateTimeseries(timeseries, 6);
        } else {
            return aggregateTimeseries(timeseries, 24);
        }
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

    private List<Timeseries> aggregateTimeseries(List<Timeseries> timeseriesList, int targetAggregatedHour) {
        if (timeseriesList.isEmpty()) {
            return timeseriesList;
        }

        final List<Timeseries> aggregated = new ArrayList<>();

        double aggregatedValue = 0;
        short count = 0;
        byte newHours = 0;
        Date previousValueTime = timeseriesList.get(0).getValueTime();
        Date previousAggregatedTime = timeseriesList.get(0).getValueTime();

        for (int i = 0; i < timeseriesList.size(); i++) {
            var timeseries = timeseriesList.get(i);

            cal.setTime(timeseries.getValueTime());
            var hour = cal.get(Calendar.HOUR_OF_DAY);
            cal.setTime(previousValueTime);
            var previousHour = cal.get(Calendar.HOUR_OF_DAY);

            if (hour != previousHour) {
                newHours++;
            }

            if (newHours == targetAggregatedHour || i == timeseriesList.size() - 1) {
                var agg = new Timeseries(timeseries);
                cal.setTime(previousValueTime);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                agg.setValueTime(previousAggregatedTime);
                agg.setValue(Math.ceil(aggregatedValue / count));
                aggregated.add(agg);
                count = 0;
                aggregatedValue = 0;
                newHours = 0;
                previousAggregatedTime = timeseries.getValueTime();
            }

            count++;
            aggregatedValue += timeseries.getValue();
            previousValueTime = timeseries.getValueTime();
        }
        return aggregated;
    }
}
