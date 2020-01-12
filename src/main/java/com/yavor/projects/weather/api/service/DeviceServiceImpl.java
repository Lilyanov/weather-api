package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.dto.DeviceDto;
import com.yavor.projects.weather.api.dto.DeviceStatus;
import com.yavor.projects.weather.api.dto.ScheduleDto;
import com.yavor.projects.weather.api.entity.Device;
import com.yavor.projects.weather.api.entity.Schedule;
import com.yavor.projects.weather.api.repository.DeviceRepository;
import com.yavor.projects.weather.api.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceImpl.class);


    private DeviceRepository deviceRepository;
    private ScheduleRepository scheduleRepository;
    private MqttService mqttService;
    private RealTimeService realTimeService;


    public DeviceServiceImpl(DeviceRepository deviceRepository,
                             ScheduleRepository scheduleRepository,
                             MqttService mqttService,
                             RealTimeService realTimeService) {
        this.deviceRepository = deviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.mqttService = mqttService;
        this.realTimeService = realTimeService;
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
        LOGGER.info("Switching device {} to status: {}", deviceId, status.getLampStatus());
        status.setDeviceId(deviceId);
        var receivedStatus = mqttService.publishLampControl(status);
        if (receivedStatus == null) {
            throw new IllegalArgumentException("Unknown status received");
        }
        var device = findDeviceEntityById(receivedStatus.getDeviceId());
        device.setStatus(receivedStatus.getLampStatus());
        device.setLastStatusChange(new Date());
        deviceRepository.save(device);

        sendDeviceRealTime(device);
    }

    @Override
    public List<ScheduleDto> scheduleDeviceSwitch(String deviceId, List<ScheduleDto> schedules) {
        var device = findDeviceEntityById(deviceId);
        var existingSchedules = device.getSchedules();
        // remove existing entities
        var removed = existingSchedules.stream()
                .filter(schedule -> schedules.stream().noneMatch(s -> s.getId() == schedule.getId()))
                .collect(Collectors.toList());
        removed.forEach(schedule -> {
            existingSchedules.remove(schedule);
            scheduleRepository.delete(schedule);
        });
        scheduleRepository.flush();

        schedules.stream()
                .filter(newSchedule -> existingSchedules.stream().noneMatch(s -> s.equalToDto(newSchedule)))
                .forEach(schedule -> {
                    if (schedule.getScheduledFor() == null) {
                        throw new IllegalArgumentException("Scheduled for is required!");
                    }
                    schedule.setCreatedAt(new Date());

                    var scheduleEntity = new Schedule();
                    scheduleEntity.setId(schedule.getId());
                    scheduleEntity.setDesiredStatus(schedule.getDesiredStatus());
                    scheduleEntity.setDevice(device);
                    scheduleEntity.setScheduledFor(schedule.getScheduledFor());
                    scheduleEntity.setCreatedAt(schedule.getCreatedAt());
                    scheduleEntity.setType(schedule.getType());
                    scheduleRepository.save(scheduleEntity);
                });


        var dto = new DeviceDto(device);
        dto.setSchedules(schedules);
        realTimeService.sendDevice(dto);

        return schedules;
    }

    @Scheduled(cron = "0 * * * * *")
    public void processScheduledSwitches() {
        LOGGER.info("start processing schedules");
        var cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        var now = cal.getTime();
        var nonRepeatedSchedules = scheduleRepository.findSchedulesByType("NON_REPEATED");
        for (var schedule : nonRepeatedSchedules) {
            if (schedule.getScheduledFor().before(now)) {
                schedule.setType("DISABLED");
                scheduleRepository.save(schedule);
                continue;
            }
            if (schedule.getScheduledFor().after(now)) {
                continue;
            }
            try {
                switchDevice(schedule.getDevice().getDeviceId(), new DeviceStatus(schedule.getDesiredStatus()));
                schedule.setType("DISABLED");
                schedule.setCreatedAt(now);
                scheduleRepository.save(schedule);
            } catch (Exception e) {
                LOGGER.error("Couldn't switch successfully device {} to {}, because of the following reason: {}",
                        schedule.getDevice().getDeviceId(), schedule.getDesiredStatus(), e.getMessage());
            }
        }

        var repeatedSchedules = scheduleRepository.findSchedulesByType("REPEATED");
        var currentHour = cal.get(Calendar.HOUR_OF_DAY);
        var currentMinute = cal.get(Calendar.MINUTE);
        for (Schedule schedule : repeatedSchedules) {
            cal.setTime(schedule.getScheduledFor());
            if (cal.get(Calendar.HOUR_OF_DAY) == currentHour && cal.get(Calendar.MINUTE) == currentMinute) {
                try {
                    switchDevice(schedule.getDevice().getDeviceId(), new DeviceStatus(schedule.getDesiredStatus()));
                } catch (Exception e) {
                    LOGGER.error("Couldn't switch successfully device {} to {}, because of the following reason: {}",
                            schedule.getDevice().getDeviceId(), schedule.getDesiredStatus(), e.getMessage());
                }
            }
        }
    }

    private void sendDeviceRealTime(Device device) {
        var deviceDto = new DeviceDto(device);
        for (var schedule : device.getSchedules()) {
            deviceDto.getSchedules().add(new ScheduleDto(schedule));
        }
        realTimeService.sendDevice(deviceDto);

    }

    private Device findDeviceEntityById(String deviceId) {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (deviceOptional.isEmpty()) {
            throw new IllegalArgumentException("Device with ID " + deviceId + " doesn't exist!");
        }
        return deviceOptional.get();
    }
}
