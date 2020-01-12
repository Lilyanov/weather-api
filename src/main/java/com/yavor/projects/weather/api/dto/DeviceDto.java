package com.yavor.projects.weather.api.dto;

import com.yavor.projects.weather.api.entity.Device;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class DeviceDto implements Serializable {

    private String deviceId;

    private Short status;

    private Date lastStatusChange;

    private List<ScheduleDto> schedules;

    public DeviceDto() {
        this.schedules = new ArrayList<>();
    }

    public DeviceDto(Device device) {
        this();
        this.deviceId = device.getDeviceId();
        this.status = device.getStatus();
        this.lastStatusChange = device.getLastStatusChange();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Date getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(Date lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }

    public List<ScheduleDto> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleDto> schedules) {
        this.schedules = schedules;
    }
}
