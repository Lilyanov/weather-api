package com.yavor.projects.weather.api.entity.pk;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class SchedulePK implements Serializable {

    private Date scheduledFor;

    private String deviceId;

    private String type;

    public SchedulePK() {

    }

    public Date getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(Date scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchedulePK that = (SchedulePK) o;
        return Objects.equals(scheduledFor, that.scheduledFor) &&
                Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduledFor, deviceId, type);
    }
}
