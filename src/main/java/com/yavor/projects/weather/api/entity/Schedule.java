package com.yavor.projects.weather.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "schedules")
@XmlRootElement
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedules_gen")
    @SequenceGenerator(name="schedules_gen", sequenceName = "schedules_seq", allocationSize = 50)
    private long id;

    @Column(name = "scheduled_for")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledFor;

    @Column(name = "type")
    private String type;

    @Column(name = "desired_status")
    private Short desiredStatus;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_fk")
    private Device device;


    public Schedule() {

    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var schedule = (Schedule) o;
        return id == schedule.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
