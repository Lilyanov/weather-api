package com.yavor.projects.weather.api.entity;

import com.yavor.projects.weather.api.entity.pk.SchedulePK;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "schedules")
@IdClass(SchedulePK.class)
@XmlRootElement
public class Schedule {

    @Id
    @Column(name = "device_fk")
    private String deviceId;

    @Id
    @Column(name = "scheduled_for")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledFor;

    @Column(name = "desired_status")
    private Short desiredStatus;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column
    private String state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_fk")
    @MapsId(value = "device_fk")
    private Device device;


    public Schedule() {

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(deviceId, schedule.deviceId) &&
                Objects.equals(scheduledFor, schedule.scheduledFor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, scheduledFor);
    }
}
