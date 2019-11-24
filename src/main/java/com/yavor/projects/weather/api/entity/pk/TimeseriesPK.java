package com.yavor.projects.weather.api.entity.pk;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


public class TimeseriesPK implements Serializable {

    private Date valueTime;

    private String type;

    private String device;

    public TimeseriesPK() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Date getValueTime() {
        return valueTime;
    }

    public void setValueTime(Date valueTime) {
        this.valueTime = valueTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeseriesPK that = (TimeseriesPK) o;
        return Objects.equals(valueTime, that.valueTime) &&
                Objects.equals(type, that.type) &&
                Objects.equals(device, that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueTime, type, device);
    }
}