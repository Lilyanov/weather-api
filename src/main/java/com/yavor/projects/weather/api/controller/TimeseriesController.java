package com.yavor.projects.weather.api.controller;

import com.yavor.projects.weather.api.dto.TimeseriesGroup;
import com.yavor.projects.weather.api.entity.Timeseries;
import com.yavor.projects.weather.api.service.TimeseriesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/timeseries")
public class TimeseriesController {

    private final TimeseriesService timeseriesService;

    public TimeseriesController(TimeseriesService timeseriesService) {
        this.timeseriesService = timeseriesService;
    }

    @GetMapping
    public ResponseEntity<List<TimeseriesGroup>> findByTypeForPerid(
            @RequestParam("types") List<String> types,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to) {

        var timeseries = timeseriesService.findTimeseriesGroups(types, from, to);
        return new ResponseEntity<>(timeseries, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<Timeseries>> save(@RequestBody List<Timeseries> timeseries) {
        var createdTimeSeries = timeseriesService.save(timeseries);
        return new ResponseEntity<>(createdTimeSeries, HttpStatus.CREATED);
    }
}
