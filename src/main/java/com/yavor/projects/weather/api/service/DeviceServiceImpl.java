package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.dto.DeviceDto;
import com.yavor.projects.weather.api.dto.DeviceStatus;
import com.yavor.projects.weather.api.dto.ScheduleDto;
import com.yavor.projects.weather.api.entity.Device;
import com.yavor.projects.weather.api.entity.Schedule;
import com.yavor.projects.weather.api.repository.DeviceRepository;
import com.yavor.projects.weather.api.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    private DeviceRepository deviceRepository;
    private ScheduleRepository scheduleRepository;


    public DeviceServiceImpl(DeviceRepository deviceRepository, ScheduleRepository scheduleRepository) {
        this.deviceRepository = deviceRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public DeviceDto findDeviceById(String deviceId) {
        var device = findDeviceEntityById(deviceId);
        return new DeviceDto(device);
    }

    @Override
    public List<DeviceDto> findAllDevices() {
        return deviceRepository.findAll().stream()
                .map(d -> {
                    var dto = new DeviceDto(d);
                    var schedulesDtos = d.getSchedules().stream()
                            .map(ScheduleDto::new).collect(Collectors.toList());
                    dto.setSchedules(schedulesDtos);
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public void switchDevice(String deviceId, DeviceStatus status) {
        var device = findDeviceEntityById(deviceId);
        device.setStatus(status.getLampStatus());
        device.setLastStatusChange(new Date());
        deviceRepository.save(device);
    }

    @Override
    public ScheduleDto scheduleDeviceSwitch(String deviceId, DeviceStatus status) {
        if (status.getScheduledDate() == null) {
            throw new IllegalArgumentException("Scheduled date is required!");
        }
        Device device = findDeviceEntityById(deviceId);
        Schedule schedule = new Schedule();
        schedule.setDesiredStatus(status.getLampStatus());
        schedule.setDevice(device);
        schedule.setDeviceId(deviceId);
        schedule.setScheduledFor(status.getScheduledDate());
        schedule.setCreatedAt(new Date());
        schedule.setState("TODO");
        schedule = scheduleRepository.save(schedule);
        return new ScheduleDto(schedule);
    }

    private Device findDeviceEntityById(String deviceId) {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (deviceOptional.isEmpty()) {
            throw new IllegalArgumentException("Device with ID " + deviceId + " doesn't exist!");
        }
        return deviceOptional.get();
    }
}
