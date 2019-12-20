package com.yavor.projects.weather.api.repository;

import com.yavor.projects.weather.api.entity.Schedule;
import com.yavor.projects.weather.api.entity.pk.SchedulePK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, SchedulePK> {

}
