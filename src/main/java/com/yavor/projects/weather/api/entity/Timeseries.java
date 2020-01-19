package com.yavor.projects.weather.api.entity;

import com.yavor.projects.weather.api.entity.pk.TimeseriesPK;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "timeseries")
@IdClass(TimeseriesPK.class)
public class Timeseries implements Serializable {

    @Id
    @Column(name = "value_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date valueTime;

    @Id
    @Column
    private String type;

    @Id
    @Column
    private String device;

    @Column
    private double value;

    @Column(name = "insertion_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertionTime;

    public Timeseries(Timeseries other) {
        this.value = other.value;
        this.valueTime = other.valueTime;
        this.device = other.device;
        this.type = other.type;
        this.insertionTime = other.insertionTime;
    }

    public Timeseries() {

    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Date insertionTime) {
        this.insertionTime = insertionTime;
    }

    public Date getValueTime() {
        return valueTime;
    }

    public void setValueTime(Date valueTime) {
        this.valueTime = valueTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeseries that = (Timeseries) o;
        return Objects.equals(valueTime, that.valueTime) &&
                Objects.equals(type, that.type) &&
                Objects.equals(device, that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueTime, type, device);
    }
}