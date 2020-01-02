package com.yavor.projects.weather.api.dto;

import com.yavor.projects.weather.api.entity.Schedule;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement
public class ScheduleDto implements Serializable {

    private Date scheduledFor;

    private Short desiredStatus;

    private Date createdAt;

    private String type;

    private long id;


    public ScheduleDto() {

    }

    public ScheduleDto(Schedule schedule) {
        this.id = schedule.getId();
        this.scheduledFor = schedule.getScheduledFor();
        this.desiredStatus = schedule.getDesiredStatus();
        this.createdAt = schedule.getCreatedAt();
        this.type = schedule.getType();
    }

    public Date getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(Date scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public Short getDesiredStatus() {
        return desiredStatus;
    }

    public void setDesiredStatus(Short desiredStatus) {
        this.desiredStatus = desiredStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
