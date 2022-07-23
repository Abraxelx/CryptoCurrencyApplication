package com.abraxel.cryptocurrency.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ChartData {
    private long timeStamp;
    private double cost;
}
