package com.example.wearther;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class houlyTemp {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long timestamp;       // 예: 1681974000000 (2025-04-12 15:00)
    public double temp;

    public int weatherInfoId;    // WeatherInfo와 연결 (외래키)
}
