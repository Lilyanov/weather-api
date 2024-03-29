CREATE TABLE IF NOT EXISTS timeseries (
	device VARCHAR(50),
	type VARCHAR(50),
	value_time TIMESTAMP WITH TIME ZONE,
	insertion_time TIMESTAMP WITH TIME ZONE,
	value float8,
	
	PRIMARY KEY(device, type, value_time)
);

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) NOT NULL,
    "password" VARCHAR(50) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role VARCHAR(100),

    PRIMARY KEY(username)
);

CREATE TABLE IF NOT EXISTS devices (
    device_id VARCHAR(50) NOT NULL,
    status smallint,
    last_status_change TIMESTAMP WITH TIME ZONE,

    PRIMARY KEY(device_id)
);

CREATE SEQUENCE schedules_seq
INCREMENT 1
MINVALUE 50
START 50;

CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT PRIMARY KEY,
    device_fk VARCHAR(50) NOT NULL,
    scheduled_for TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    type VARCHAR(100),
    desired_status smallint,

    FOREIGN KEY(device_fk) REFERENCES devices(device_id)
);
