package com.example.wearther;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WeatherInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double temp;
    public double windSpeed;
    public boolean isRaining;
    public boolean isSnowing;

    public long timestamp;
}
