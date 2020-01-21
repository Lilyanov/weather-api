package com.yavor.projects.weather.api.repository;

import com.yavor.projects.weather.api.entity.Timeseries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TimeseriesRepository extends JpaRepository<Timeseries, Long> {

    @Query("SELECT t FROM Timeseries t WHERE t.type = ?1 AND t.valueTime >= ?2 AND date_trunc('minute', t.valueTime) <= ?3 ORDER BY t.valueTime ASC")
    List<Timeseries> findTimeseriesByTypeForPeriod(String type, Date from, Date to);

    @Query("SELECT t.device, t.type, date_trunc('hour', t.valueTime) as value_time, AVG(t.value) as value" +
            " FROM Timeseries t" +
            " WHERE t.type = ?1 AND t.valueTime >= ?2 AND date_trunc('minute', t.valueTime) <= ?3" +
            " GROUP BY t.device, t.type, date_trunc('hour', t.valueTime)" +
            " ORDER BY date_trunc('hour', t.valueTime)")
    List<Object[]> groupTimeseriesByHourForPeriod(String type, Date from, Date to);

    @Query("SELECT t.device, t.type, date_trunc('day', t.valueTime) as value_time, AVG(t.value) as value" +
            " FROM Timeseries t" +
            " WHERE t.type = ?1 AND t.valueTime >= ?2 AND date_trunc('minute', t.valueTime) <= ?3" +
            " GROUP BY t.device, t.type, date_trunc('day', t.valueTime)" +
            " ORDER BY date_trunc('day', t.valueTime)")
    List<Object[]> groupTimeseriesByDayForPeriod(String type, Date from, Date to);
}
