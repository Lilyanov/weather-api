package com.yavor.projects.weather.api.repository;

import com.yavor.projects.weather.api.entity.Schedule;
import com.yavor.projects.weather.api.entity.pk.SchedulePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, SchedulePK> {

    @Query("SELECT s FROM Schedule s WHERE s.type = ?1 AND s.scheduledFor = ?2")
    List<Schedule> findFutureSchedulesByType(String type, Date now);

    @Query("SELECT s FROM Schedule s WHERE s.type = ?1")
    List<Schedule> findSchedulesByType(String type);
}
