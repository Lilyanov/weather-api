package com.yavor.projects.weather.api.repository;

import com.yavor.projects.weather.api.entity.Timeseries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeseriesRepository extends JpaRepository<Timeseries, Long> {

}
