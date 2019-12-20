package com.yavor.projects.weather.api.dto;

import java.util.Date;

public class DeviceStatus {

    private short lampStatus;

    private Date scheduledDate;


    public DeviceStatus() {

    }

    public short getLampStatus() {
        return lampStatus;
    }

    public void setLampStatus(short lampStatus) {
        this.lampStatus = lampStatus;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
