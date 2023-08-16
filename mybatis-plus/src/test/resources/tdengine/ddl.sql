alter local 'timezone' 'UTC-8';
alter local 'keepColumnName' '1';

create database if not exists test cachemodel 'both' keep 365000;
use test;

create stable device_data (
    ts timestamp,
    raw_data binary(1024),
    voltage double
)
tags (
    device_no nchar(256),
    device_name nchar(256)
);
