package com.yavor.projects.weather.api.repository;

import com.yavor.projects.weather.api.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {

}
