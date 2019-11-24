CREATE TABLE IF NOT EXISTS timeseries (
	device VARCHAR(50),
	type VARCHAR(50),
	value_time TIMESTAMP WITH TIME ZONE,
	insertion_time TIMESTAMP WITH TIME ZONE,
	value float8,
	
	PRIMARY KEY(device, type, value_time)
);

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50),
    "password" VARCHAR(50) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),

    PRIMARY KEY(username)
);