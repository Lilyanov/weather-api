package com.yavor.projects.weather.api.repository;

import com.yavor.projects.weather.api.entity.Timeseries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TimeseriesRepository extends JpaRepository<Timeseries, Long> {

    @Query("SELECT t FROM Timeseries t WHERE t.type = ?1 AND t.valueTime >= ?2 AND t.valueTime < ?3 ORDER BY t.valueTime ASC")
    List<Timeseries> findTimeseriesByTypeForPeriod(String type, Date from, Date to);

}
